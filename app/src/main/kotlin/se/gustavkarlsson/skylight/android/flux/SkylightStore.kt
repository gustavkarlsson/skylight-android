package se.gustavkarlsson.skylight.android.flux

import io.reactivex.ObservableTransformer

class SkylightStore(
	initialState: State,
	transformers: Iterable<ObservableTransformer<Action, Result>>,
	reducer: (current: State, result: Result) -> State,
	vararg startActions: Action
) : Store<State, Action, Result>(initialState, transformers, reducer, *startActions)
