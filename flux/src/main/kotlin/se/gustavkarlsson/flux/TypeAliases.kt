package se.gustavkarlsson.flux

import io.reactivex.Observable

internal typealias CommandTransformer<Command, Result> = (Observable<Command>) -> Observable<Result>

internal typealias CommandWithStateTransformer<State, Command, Result> =
		(Observable<State>, Observable<Command>) -> Observable<Result>

internal typealias StateWatcher<State> = (State) -> Unit
