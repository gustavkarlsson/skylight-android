package se.gustavkarlsson.skylight.android.feature.settings

import com.ioki.textref.TextRef
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.settings.SettingsRepository
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

internal class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : CoroutineScopedService() {
    val state: StateFlow<ViewState> = settingsRepository.stream()
        .map { settings ->
            ViewState(
                triggerLevelText = settings.notificationTriggerLevel.shortText,
                triggerLevelItems = createTriggerLevelItems(settings.notificationTriggerLevel)
            )
        }
        .stateIn(scope, started = SharingStarted.WhileSubscribed(), ViewState.INITIAL)

    fun onTriggerLevelChanged(triggerLevel: TriggerLevel) {
        scope.launch {
            settingsRepository.setNotificationTriggerLevel(triggerLevel)
        }
    }
}

private fun createTriggerLevelItems(notificationTriggerLevel: TriggerLevel): List<TriggerLevelItem> {
    return TriggerLevel.values()
        .sortedBy { it.displayIndex }
        .map { triggerLevel ->
            val text = triggerLevel.shortText
            val selected = triggerLevel == notificationTriggerLevel
            TriggerLevelItem(triggerLevel, text, selected)
        }
}

private val TriggerLevel.displayIndex: Int
    get() = when (this) {
        TriggerLevel.HIGH -> 1
        TriggerLevel.MEDIUM -> 2
        TriggerLevel.LOW -> 3
    }

private val TriggerLevel.shortText: TextRef
    get() = when (this) {
        TriggerLevel.LOW -> TextRef.stringRes(R.string.notify_at_low)
        TriggerLevel.MEDIUM -> TextRef.stringRes(R.string.notify_at_medium)
        TriggerLevel.HIGH -> TextRef.stringRes(R.string.notify_at_high)
    }

internal data class ViewState(
    val triggerLevelText: TextRef,
    val triggerLevelItems: List<TriggerLevelItem>,
) {
    companion object {
        val INITIAL = ViewState(TextRef.EMPTY, emptyList())
    }
}

internal data class TriggerLevelItem(
    val triggerLevel: TriggerLevel,
    val text: TextRef,
    val selected: Boolean,
)
