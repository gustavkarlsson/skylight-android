package se.gustavkarlsson.skylight.android.feature.addplace

import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdateState

internal class SetQueryAction(private val query: String) : Action<State> {
    override suspend fun execute(updateState: UpdateState<State>) {
        updateState { state ->
            val query = query.trim()
            state.copy(query = query)
        }
    }
}
