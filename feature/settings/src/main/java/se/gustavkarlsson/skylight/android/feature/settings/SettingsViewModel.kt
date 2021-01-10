package se.gustavkarlsson.skylight.android.feature.settings

import com.ioki.textref.TextRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
internal class SettingsViewModel @Inject constructor(
    private val settings: Settings
) : CoroutineScopedService() {

    private val showSelectTriggerLevelChannel = BroadcastChannel<Pair<Place, TriggerLevel>>(Channel.BUFFERED)
    val showSelectTriggerLevel: Flow<Pair<Place, TriggerLevel>> = showSelectTriggerLevelChannel.asFlow()

    val settingsItems: Flow<List<SettingsItem>> =
        settings.streamNotificationTriggerLevels()
            .map { levels ->
                val triggerLevelItems = levels.map { (place, triggerLevel) ->
                    SettingsItem.TriggerLevelItem(
                        place.name,
                        triggerLevel.longText,
                        place,
                        triggerLevel
                    )
                }
                listOf(SettingsItem.TitleItem) + triggerLevelItems
            }

    fun onTriggerLevelItemClicked(place: Place, triggerLevel: TriggerLevel) {
        showSelectTriggerLevelChannel.offer(place to triggerLevel)
    }

    fun onTriggerLevelSelected(place: Place, triggerLevel: TriggerLevel) {
        scope.launch {
            settings.setNotificationTriggerLevel(place, triggerLevel)
        }
    }
}

internal sealed class SettingsItem {
    object TitleItem : SettingsItem()

    data class TriggerLevelItem(
        val title: TextRef,
        val subtitle: TextRef,
        val place: Place,
        val triggerLevel: TriggerLevel
    ) : SettingsItem()
}

private val TriggerLevel.longText: TextRef
    get() = when (this) {
        TriggerLevel.NEVER -> TextRef.stringRes(R.string.pref_notifications_entry_never_long)
        TriggerLevel.LOW -> TextRef.stringRes(R.string.pref_notifications_entry_low_long)
        TriggerLevel.MEDIUM -> TextRef.stringRes(R.string.pref_notifications_entry_medium_long)
        TriggerLevel.HIGH -> TextRef.stringRes(R.string.pref_notifications_entry_high_long)
    }
