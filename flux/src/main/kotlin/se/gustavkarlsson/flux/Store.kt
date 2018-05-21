package se.gustavkarlsson.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class Store<State : Any, Action : Any, Result : Any>
internal constructor(
	initialState: () -> State,
	actionTransformers: List<ActionTransformer<Action, Result>>,
	actionWithStateTransformers: List<ActionWithStateTransformer<State, Action, Result>>,
	resultReducers: List<ResultReducer<State, Result>>,
	observeScheduler: Scheduler?,
	reduceScheduler: Scheduler
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
		.publish {
			it.compose(
				ActionToResultTransformer(
					states,
					actionTransformers,
					actionWithStateTransformers
				)
			)
		}

	private val states = results
		.observeOn(reduceScheduler)
		.scanWith(initialState, CompositeReducer(resultReducers))
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
