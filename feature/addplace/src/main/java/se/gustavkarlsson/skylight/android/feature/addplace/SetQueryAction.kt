package se.gustavkarlsson.skylight.android.feature.addplace

import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow

internal class SetQueryAction(private val query: String) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        state.update {
            val trimmedQuery = this@SetQueryAction.query.trim()
            copy(query = trimmedQuery)
        }
    }
}
