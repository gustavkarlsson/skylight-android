package se.gustavkarlsson.skylight.android.feature.addplace

import se.gustavkarlsson.conveyor.Change
import se.gustavkarlsson.conveyor.Command
import se.gustavkarlsson.conveyor.only

internal class SetQueryCommand(private val query: String) : Command<State> {
    override fun reduce(state: State): Change<State> {
        val query = query.trim()
        return state.copy(query = query).only()
    }
}
