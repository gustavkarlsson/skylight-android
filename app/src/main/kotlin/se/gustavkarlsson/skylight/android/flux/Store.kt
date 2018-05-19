package se.gustavkarlsson.skylight.android.flux

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType

open class Store<State : Any, Action : Any, Result : Any>
private constructor(
	initialState: State,
	transformers: List<(Observable<Action>) -> Observable<Result>>,
	reducer: (current: State, result: Result) -> State,
	observeScheduler: Scheduler?,
	private val initialActions: List<Action>
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
			val results = transformers
				.map { actions.compose(it) }
			Observable.merge(results)
		}

	private val connectableStates = results
		.scan(initialState, reducer)
		.run {
			if (observeScheduler != null) {
				observeOn(observeScheduler)
			} else {
				this
			}
		}
		.replay(1)

	val states: Observable<State>
		@Synchronized
		get() {
			if (statesSubscription == null) {
				throw IllegalStateException("Can't observe state until started")
			}
			return connectableStates
		}

	@Synchronized
	fun start() {
		if (statesSubscription != null) return
		statesSubscription = connectableStates.subscribe()
		connectableStates.connect()
		initialActions.forEach(::postAction)
	}

	class Builder<State : Any, Action : Any, Result : Any>(
		private val initialState: State,
		private val reducer: (current: State, result: Result) -> State
	) {

		private val transformers: MutableList<(Observable<Action>) -> Observable<Result>> =
			mutableListOf()
		private val initialActions: MutableList<Action> = mutableListOf()
		private var observeScheduler: Scheduler? = null

		fun addActionTransformer(
			transformer: (Observable<Action>) -> Observable<Result>
		): Builder<State, Action, Result> =
			apply {
				transformers += transformer
			}

		@JvmName("addActionTransformer6")
		inline fun <reified A : Action, reified R : Result> flatMapAction(
			noinline mapper: (A) -> Observable<R>
		): Builder<State, Action, Result> =
			apply {
				addActionTransformer { actions: Observable<Action> ->
					actions.ofType<A>().flatMap(mapper)
				}
			}

		@JvmName("addActionTransformer7")
		inline fun <reified A : Action, reified R : Result> switchMapAction(
			noinline mapper: (A) -> Observable<R>
		): Builder<State, Action, Result> =
			apply {
				addActionTransformer { actions: Observable<Action> ->
					actions.ofType<A>().switchMap(mapper)
				}
			}

		@JvmName("addActionTransformer8")
		inline fun <reified A : Action, reified R : Result> mapAction(
			noinline mapper: (A) -> R
		): Builder<State, Action, Result> =
			apply {
				addActionTransformer { actions: Observable<Action> ->
					actions.ofType<A>().map(mapper)
				}
			}

		fun addInitialActions(
			vararg actions: Action
		): Builder<State, Action, Result> =
			apply {
				initialActions += actions
			}

		fun setObserveScheduler(
			scheduler: Scheduler?
		): Builder<State, Action, Result> =
			apply {
				observeScheduler = scheduler
			}

		fun build(): Store<State, Action, Result> {
			if (transformers.isEmpty()) throw IllegalStateException("No transformers added")
			return Store(initialState, transformers, reducer, observeScheduler, initialActions)
		}
	}
}
