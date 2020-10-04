package se.gustavkarlsson.skylight.android.feature.addplace

import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.StateAccess

internal class SetQueryAction(private val query: String) : Action<State> {
    override suspend fun execute(stateAccess: StateAccess<State>) {
        stateAccess.update { state ->
            val query = query.trim()
            state.copy(query = query)
        }
    }
}
