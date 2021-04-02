package se.gustavkarlsson.skylight.android.feature.main.state

import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.lib.settings.Settings

internal class StreamTriggerLevelsAction @Inject constructor(
    private val settings: Settings,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        settings.streamNotificationTriggerLevels().collect { levels ->
            state.update {
                copy(notificationTriggerLevels = levels.toMap().mapKeys { entry -> entry.key.id })
            }
        }
    }
}
