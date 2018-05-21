package se.gustavkarlsson.skylight.android.flux

import io.reactivex.Observable

internal class ActionToResultTransformer<State : Any, Action : Any, Result : Any>(
	private val states: Observable<State>,
	private val actionTransformers: List<ActionTransformer<Action, Result>>,
	private val actionWithStateTransformers: List<ActionWithStateTransformer<State, Action, Result>>
) : (Observable<Action>) -> Observable<Result> {

	override fun invoke(actions: Observable<Action>): Observable<Result> {
		val actionResults = actionTransformers
			.map { it(actions) }
		val actionWithStateResults = actionWithStateTransformers
			.map { it(states, actions) }
		return Observable.merge(actionResults + actionWithStateResults)
	}
}
