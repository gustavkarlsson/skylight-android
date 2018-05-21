package se.gustavkarlsson.flux

internal class CompositeReducer<State : Any, Result : Any>(
	private val resultReducers: List<ResultReducer<State, Result>>
) : (State, Result) -> State {

	override fun invoke(currentState: State, result: Result): State {
		var newState = currentState
		resultReducers
			.filter { it.clazz.isInstance(result) }
			.map { it.reducer }
			.forEach {
				newState = it(newState, result)
			}
		return newState
	}
}
