package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.collectLatest
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.lib.settings.SettingsRepository

@Inject
internal class StreamTriggerLevelsAction(
    private val settingsRepository: SettingsRepository,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        settingsRepository.stream().collectLatest { levels ->
            stateFlow.update {
                when (this) {
                    is State.Loading -> copy(settings = levels)
                    is State.Ready -> copy(settings = levels)
                }
            }
        }
    }
}
