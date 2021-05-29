package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collect
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import javax.inject.Inject

internal class StreamTriggerLevelsAction @Inject constructor(
    private val settings: Settings,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        settings.streamNotificationTriggerLevels().collect { levels ->
            stateFlow.update {
                copy(notificationTriggerLevels = levels)
            }
        }
    }
}
