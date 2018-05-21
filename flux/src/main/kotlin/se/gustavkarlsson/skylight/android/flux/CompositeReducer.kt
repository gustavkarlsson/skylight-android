package se.gustavkarlsson.skylight.android.flux


internal class CompositeReducer<State : Any, Result : Any>(
	private val resultReducers: List<Pair<Class<out Result>, (State, Result) -> State>>
) : (State, Result) -> State {
	override fun invoke(currentState: State, result: Result): State {
		var newState = currentState
		resultReducers
			.filter { it.first.isInstance(result) }
			.map { it.second }
			.forEach {
				newState = it(newState, result)
			}
		return newState
	}
}
