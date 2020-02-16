package se.gustavkarlsson.skylight.android.feature.settings

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.services.Settings

internal class SettingsViewModel(private val settings: Settings) : ViewModel() {

	private val disposables = CompositeDisposable()

	private val showSelectTriggerLevelRelay = PublishRelay.create<Place>()
	val showSelectTriggerLevel: Observable<Place> = showSelectTriggerLevelRelay

	val triggerLevelItems: Observable<List<TriggerLevelItem>> =
		settings.streamNotificationTriggerLevels()
			.map { levels ->
				levels.map { (place, triggerLevel) ->
					TriggerLevelItem(place.name, triggerLevel.longText, place)
				}
			}
			.observeOn(AndroidSchedulers.mainThread())

	fun onItemClicked(place: Place) {
		showSelectTriggerLevelRelay.accept(place)
	}

	fun onTriggerLevelSelected(place: Place, triggerLevel: TriggerLevel) {
		disposables += settings.setNotificationTriggerLevel(place, triggerLevel).subscribe()
	}

	override fun onCleared() = disposables.clear()
}

internal data class TriggerLevelItem(
	val title: TextRef,
	val subtitle: TextRef,
	val place: Place
)

private val TriggerLevel.longText: TextRef
	get() = when (this) {
		TriggerLevel.NEVER -> TextRef(R.string.pref_notifications_entry_never_long)
		TriggerLevel.LOW -> TextRef(R.string.pref_notifications_entry_low_long)
		TriggerLevel.MEDIUM -> TextRef(R.string.pref_notifications_entry_medium_long)
		TriggerLevel.HIGH -> TextRef(R.string.pref_notifications_entry_high_long)
	}
