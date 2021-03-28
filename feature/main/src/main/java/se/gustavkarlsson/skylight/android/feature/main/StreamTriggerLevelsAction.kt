package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.places.Place

internal class StreamTriggerLevelsAction(
    private val notificationTriggerLevels: Flow<List<Pair<Place, TriggerLevel>>>,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        notificationTriggerLevels.collect { levels ->
            state.update {
                copy(notificationTriggerLevels = levels.toMap().mapKeys { entry -> entry.key.id })
            }
        }
    }
}
