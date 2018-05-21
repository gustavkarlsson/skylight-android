package se.gustavkarlsson.skylight.android.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Store<State : Any, Action : Any, Result : Any>
internal constructor(
	initialState: () -> State,
	actionTransformers: List<(Observable<Action>) -> Observable<Result>>,
	actionWithStateTransformers: List<(Observable<State>, Observable<Action>) -> Observable<Result>>,
	resultReducers: List<Pair<Class<out Result>, (State, Result) -> State>>,
	observeScheduler: Scheduler?
) {
	private var statesSubscription: Disposable? = null

	fun postAction(action: Action) {
		if (statesSubscription == null) {
			throw IllegalStateException("Can't post actions until started")
		}
		actions.accept(action)
	}

	private val actions: Relay<Action> = PublishRelay.create<Action>()

	private val results: Observable<Result> = actions
		.publish { actions ->
			val actionResults =
				actionTransformers.map { it(actions) }
			val actionWithStateResults =
				actionWithStateTransformers.map { it(connectableStates, actions) }
			Observable.merge(actionResults + actionWithStateResults)
		}

	private val compositeReducer: (State, Result) -> State = { state, result ->
		val matchingReducers: List<(State, Result) -> State> =
			listOf({ s: State, _: Result -> s }) +
				resultReducers
					.filter { it.first.isInstance(result) }
					.map { it.second }

		val reducer = matchingReducers.reduce { lastReducer, currentReducer ->
			{ state: State, result: Result ->
				currentReducer(lastReducer(state, result), result)
			}
		}
		reducer(state, result)
	}

	private val connectableStates = results
		.observeOn(Schedulers.newThread()) // Scan is not thread safe so must run sequentially
		.scanWith(initialState, compositeReducer)
		.run {
			if (observeScheduler != null) {
				observeOn(observeScheduler)
			} else {
				this
			}
		}
		.replay(1)

	@Synchronized
	fun getState(): Observable<State> {
		if (statesSubscription == null) {
			throw IllegalStateException("Can't observe state until started")
		}
		return connectableStates
	}

	@Synchronized
	fun start() {
		if (statesSubscription != null) return
		connectableStates.connect()
		statesSubscription = connectableStates.subscribe()
	}
}
