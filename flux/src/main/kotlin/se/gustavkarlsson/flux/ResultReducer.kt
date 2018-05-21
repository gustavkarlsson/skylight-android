package se.gustavkarlsson.flux

internal data class ResultReducer<State : Any, Result : Any>(
	val clazz: Class<out Result>,
	val reducer: (State, Result) -> State
)
