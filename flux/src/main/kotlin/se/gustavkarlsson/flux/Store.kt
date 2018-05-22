package se.gustavkarlsson.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class Store<State : Any, Command : Any, Result : Any>
internal constructor(
	initialState: () -> State,
	commandTransformers: List<CommandTransformer<Command, Result>>,
	commandWithStateTransformers: List<CommandWithStateTransformer<State, Command, Result>>,
	resultReducers: List<ResultReducer<State, Result>>,
	stateWatchers: List<StateWatcher<State>>,
	observeScheduler: Scheduler?,
	reduceScheduler: Scheduler
) {
	private var statesSubscription: Disposable? = null

	fun post(issue: Command) {
		if (statesSubscription == null) {
			throw IllegalStateException("Can't post commands until started")
		}
		commands.accept(issue)
	}

	private val commands: Relay<Command> = PublishRelay.create<Command>()

	private val results: Observable<Result> = commands
		.publish {
			it.compose(
				CommandToResultTransformer(
					states,
					commandTransformers,
					commandWithStateTransformers
				)
			)
		}

	private val states = results
		.observeOn(reduceScheduler)
		.scanWith(initialState, CompositeReducer(resultReducers))
		.doOnNext { state -> stateWatchers.forEach { it(state) } }
		.let {
			if (observeScheduler != null) {
				it.observeOn(observeScheduler)
			} else {
				it
			}
		}
		.replay(1)

	@Synchronized
	fun getState(): Observable<State> {
		if (statesSubscription == null) {
			throw IllegalStateException("Can't observe state until started")
		}
		return states
	}

	@Synchronized
	fun start() {
		if (statesSubscription != null) return
		states.connect()
		statesSubscription = states.subscribe()
	}
}
