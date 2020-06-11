package se.gustavkarlsson.skylight.android.feature.settings

import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import se.gustavkarlsson.skylight.android.lib.settings.Settings

internal class SettingsViewModel(private val settings: Settings) :
    ScopedService {

    private val disposables = CompositeDisposable()

    private val showSelectTriggerLevelRelay = PublishRelay.create<Pair<Place, TriggerLevel>>()
    val showSelectTriggerLevel: Observable<Pair<Place, TriggerLevel>> = showSelectTriggerLevelRelay

    val settingsItems: Observable<List<SettingsItem>> =
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
            .observeOn(AndroidSchedulers.mainThread())

    fun onTriggerLevelItemClicked(place: Place, triggerLevel: TriggerLevel) {
        showSelectTriggerLevelRelay.accept(place to triggerLevel)
    }

    fun onTriggerLevelSelected(place: Place, triggerLevel: TriggerLevel) {
        disposables += settings.setNotificationTriggerLevel(place, triggerLevel).subscribe()
    }

    override fun onCleared() = disposables.clear()
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
        TriggerLevel.NEVER -> TextRef(R.string.pref_notifications_entry_never_long)
        TriggerLevel.LOW -> TextRef(R.string.pref_notifications_entry_low_long)
        TriggerLevel.MEDIUM -> TextRef(R.string.pref_notifications_entry_medium_long)
        TriggerLevel.HIGH -> TextRef(R.string.pref_notifications_entry_high_long)
    }
