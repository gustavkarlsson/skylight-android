package se.gustavkarlsson.skylight.android.flux

internal data class ResultReducer<State : Any, Result : Any>(
	val clazz: Class<out Result>,
	val reducer: (State, Result) -> State
)
