package se.gustavkarlsson.skylight.android.feature.settings

import com.ioki.textref.TextRef
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.settings.Settings
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

@ExperimentalCoroutinesApi
@FlowPreview
internal class SettingsViewModel @Inject constructor(
    private val settings: Settings
) : CoroutineScopedService() {

    val settingsItems: Flow<List<SettingsItem>> =
        settings.streamNotificationTriggerLevels()
            .map { levels ->
                levels.map { (place, triggerLevel) ->
                    SettingsItem(
                        title = place.name,
                        subtitle = triggerLevel.longText,
                        place = place,
                        triggerLevel = triggerLevel,
                    )
                }
            }

    fun onTriggerLevelSelected(place: Place, triggerLevel: TriggerLevel) {
        scope.launch {
            settings.setNotificationTriggerLevel(place, triggerLevel)
        }
    }
}

internal data class SettingsItem(
    val title: TextRef,
    val subtitle: TextRef,
    val place: Place,
    val triggerLevel: TriggerLevel
)

private val TriggerLevel.longText: TextRef
    get() = when (this) {
        TriggerLevel.NEVER -> TextRef.stringRes(R.string.pref_notifications_entry_never_long)
        TriggerLevel.LOW -> TextRef.stringRes(R.string.pref_notifications_entry_low_long)
        TriggerLevel.MEDIUM -> TextRef.stringRes(R.string.pref_notifications_entry_medium_long)
        TriggerLevel.HIGH -> TextRef.stringRes(R.string.pref_notifications_entry_high_long)
    }
