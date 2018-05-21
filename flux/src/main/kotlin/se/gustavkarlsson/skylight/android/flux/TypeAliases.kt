package se.gustavkarlsson.skylight.android.flux

import io.reactivex.Observable

internal typealias ActionTransformer<Action, Result> = (Observable<Action>) -> Observable<Result>

internal typealias ActionWithStateTransformer<State, Action, Result> =
		(Observable<State>, Observable<Action>) -> Observable<Result>
