package se.gustavkarlsson.flux

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class StoreBuilder<State : Any, Command : Any, Result : Any>
internal constructor() {
	private var initialState: State? = null
	private val commandTransformers =
		mutableListOf<CommandTransformer<Command, Result>>()
	private val commandWithStateTransformers =
		mutableListOf<CommandWithStateTransformer<State, Command, Result>>()
	private val resultReducers =
		mutableListOf<ResultReducer<State, Result>>()
	private val stateWatchers =
		mutableListOf<StateWatcher<State>>()
	private var observeScheduler: Scheduler? = null

	fun initWith(initialState: State) {
		this.initialState = initialState
	}

	fun transformCommands(
		transformer: (Observable<Command>) -> Observable<Result>
	) {
		commandTransformers += transformer
	}

	inline fun <reified C : Command, R : Result> mapCommand(
		noinline mapper: (C) -> R
	) {
		transformCommands { commands: Observable<Command> ->
			commands.ofType<C>().map(mapper)
		}
	}

	inline fun <reified C : Command, R : Result> flatMapCommand(
		noinline mapper: (C) -> Observable<R>
	) {
		transformCommands { commands: Observable<Command> ->
			commands.ofType<C>().flatMap(mapper)
		}
	}

	inline fun <reified C : Command, R : Result> switchMapCommand(
		noinline mapper: (C) -> Observable<R>
	) {
		transformCommands { commands: Observable<Command> ->
			commands.ofType<C>().switchMap(mapper)
		}
	}

	fun transformCommandsWithState(
		transformers: (Observable<State>, Observable<Command>) -> Observable<Result>
	) {
		commandWithStateTransformers += transformers
	}

	inline fun <reified C : Command, R : Result> mapCommandWithState(
		noinline mapper: (C, State) -> R
	) {
		transformCommandsWithState { states: Observable<State>, commands: Observable<Command> ->
			commands.ofType<C>().withLatestFrom(states, mapper)
		}
	}

	inline fun <reified C : Command, R : Result> flatMapCommandWithState(
		noinline mapper: (C, State) -> Observable<R>
	) {
		transformCommandsWithState { states: Observable<State>, commands: Observable<Command> ->
			commands.ofType<C>().withLatestFrom(states, mapper).flatMap { it }
		}
	}

	inline fun <reified C : Command, R : Result> switchMapCommandWithState(
		noinline mapper: (C, State) -> Observable<R>
	) {
		transformCommandsWithState { states: Observable<State>, commands: Observable<Command> ->
			commands.ofType<C>().withLatestFrom(states, mapper).switchMap { it }
		}
	}

	fun <R : Result> reduceResult(clazz: Class<R>, reducer: (State, R) -> State) {
		@Suppress("UNCHECKED_CAST")
		resultReducers += ResultReducer<State, Result>(
			clazz,
			reducer as (State, Result) -> State
		)
	}

	inline fun <reified R : Result> reduceResult(noinline reducer: (State, R) -> State) {
		reduceResult(R::class.java, reducer)
	}

	fun observeOn(scheduler: Scheduler?) {
		observeScheduler = scheduler
	}

	inline fun <reified C : Command> doOnCommand(crossinline block: (C) -> Unit) {
		flatMapCommand { command: C ->
			block(command)
			Observable.empty<Result>()
		}
	}

	inline fun <reified R : Result> doOnResult(crossinline block: (R) -> Unit) {
		reduceResult { state: State, result: R ->
			block(result)
			state
		}
	}

	fun doOnState(block: (State) -> Unit) {
		stateWatchers += block
	}

	fun build(): Store<State, Command, Result> {
		val initialState = initialState
			?: throw IllegalStateException("No initial state set")
		if (commandTransformers.isEmpty() && commandWithStateTransformers.isEmpty()) {
			throw IllegalStateException("No command transformers defined")
		}
		if (resultReducers.isEmpty()) throw IllegalStateException("No result reducers defined")
		return Store(
			initialState,
			commandTransformers,
			commandWithStateTransformers,
			resultReducers,
			stateWatchers,
			observeScheduler,
			Schedulers.newThread() // Scan is not thread safe so a dedicated thread is a good idea
		)
	}
}

fun <State : Any, Command : Any, Result : Any> buildStore(
	block: StoreBuilder<State, Command, Result>.() -> Unit
): Store<State, Command, Result> {
	val builder = StoreBuilder<State, Command, Result>()
	builder.block()
	return builder.build()
}
