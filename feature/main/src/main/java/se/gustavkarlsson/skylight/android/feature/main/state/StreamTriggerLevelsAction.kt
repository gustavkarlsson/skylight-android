package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import javax.inject.Inject

internal class StreamTriggerLevelsAction @Inject constructor(
    private val settings: Settings,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        settings.streamNotificationTriggerLevels().collectLatest { levels ->
            stateFlow.update {
                when (this) {
                    is State.Loading -> copy(notificationTriggerLevels = levels)
                    is State.Ready -> copy(notificationTriggerLevels = levels)
                }
            }
        }
    }
}
