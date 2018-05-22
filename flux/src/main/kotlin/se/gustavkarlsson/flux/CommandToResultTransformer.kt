package se.gustavkarlsson.flux

import io.reactivex.Observable

internal class CommandToResultTransformer<State : Any, Command : Any, Result : Any>(
	private val states: Observable<State>,
	private val commandTransformers: List<CommandTransformer<Command, Result>>,
	private val commandWithStateTransformers: List<CommandWithStateTransformer<State, Command, Result>>
) : (Observable<Command>) -> Observable<Result> {

	override fun invoke(commands: Observable<Command>): Observable<Result> {
		val commandResults = commandTransformers
			.map { it(commands) }
		val commandWithStateResults = commandWithStateTransformers
			.map { it(states, commands) }
		return Observable.merge(commandResults + commandWithStateResults)
	}
}
