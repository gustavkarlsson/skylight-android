package se.gustavkarlsson.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

/**
 * Manages application (sub)state by accepting commands,
 * transforming them into any number of results,
 * which are then used to sequentially produce new states
 *
 * @param State The type representing the state of the store
 * @param Command Commands are issued to produce results
 * @param Result The result of a command used to produce a new state
 */
class Store<State : Any, Command : Any, Result : Any>
internal constructor(
	initialState: State,
	commandTransformers: List<CommandTransformer<Command, Result>>,
	commandWithStateTransformers: List<CommandWithStateTransformer<State, Command, Result>>,
	resultReducers: List<ResultReducer<State, Result>>,
	stateWatchers: List<StateWatcher<State>>,
	observeScheduler: Scheduler?,
	reduceScheduler: Scheduler
) {
	private var internalSubscription: Disposable? = null
	private var connection: Disposable? = null

	/**
	 * The current state of the store
	 */
	@Volatile
	var currentState: State = initialState
		private set

	/**
	 * Issue a command to the store for processing
	 *
	 * @param command the command to issue
	 * @throws IllegalStateException if store has not been [start]ed
	 */
	fun issue(command: Command) {
		if (internalSubscription == null) {
			throw IllegalStateException("Can't issue commands until started")
		}
		commands.accept(command)
	}

	private val commands: Relay<Command> = PublishRelay.create<Command>()

	private val results: Observable<Result> = commands
		.publish {
			it.compose(
				CommandToResultTransformer(
					connectableStates,
					commandTransformers,
					commandWithStateTransformers
				)
			)
		}

	private val connectableStates = results
		.observeOn(reduceScheduler)
		.scanWith({ currentState }, CompositeReducer(resultReducers))
		.doOnNext { state ->
			currentState = state
			stateWatchers.forEach { it(state) }
		}
		.let {
			if (observeScheduler != null) {
				it.observeOn(observeScheduler)
			} else {
				it
			}
		}
		.replay(1)

	/**
	 * An observable stream of state objects produced by this store, starting with the current state
	 *
	 * State objects will be observed on the observe [Scheduler] if one was specified
	 * or a per-store unique background thread.
	 *
	 * *Note: The store will not produce any state until [start]ed.*
	 *
	 * *Note: All subscribers share a single upstream subscription,
	 * so there is no need to use publishing operators such as [Observable.publish].*
	 * @return An [Observable] of [State] updates
	 */
	val states: Observable<State> get() = connectableStates

	/**
	 * Starts processing of this store.
	 * When started, commands issued via [issue] will be accepted
	 * and any observer of [states] will start receiving states.
	 */
	@Synchronized
	fun start() {
		if (isRunning) return
		connection = connectableStates.connect()
		internalSubscription = connectableStates.subscribe()
	}

	/**
	 * Stops processing of this store.
	 * When stopped, commands will no longer be accepted
	 * and [states] will stop producing state objects
	 */
	@Synchronized
	fun stop() {
		if (!isRunning) return
		internalSubscription?.dispose()
		connection?.dispose()
		internalSubscription = null
		connection = null
	}

	/**
	 * Is this store running (has been [start]ed)?
	 */
	val isRunning get() = internalSubscription != null
}
