package se.gustavkarlsson.skylight.android.flux

import io.reactivex.Observable

internal class ActionToResultTransformer<State : Any, Action : Any, Result : Any>(
	private val states: Observable<State>,
	private val actionTransformers: List<(Observable<Action>) -> Observable<Result>>,
	private val actionWithStateTransformers: List<(Observable<State>, Observable<Action>) -> Observable<Result>>
) : (Observable<Action>) -> Observable<Result> {
	override fun invoke(actions: Observable<Action>): Observable<Result> {
		val actionResults = actionTransformers
			.map { it(actions) }
		val actionWithStateResults = actionWithStateTransformers
			.map { it(states, actions) }
		return Observable.merge(actionResults + actionWithStateResults)
	}
}
