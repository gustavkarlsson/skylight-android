package se.gustavkarlsson.skylight.android.feature.settings

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.services.Settings

internal class SettingsViewModel(private val settings: Settings) : ViewModel() {

	private val disposables = CompositeDisposable()

	val settingsItems: Observable<List<Pair<Place, TriggerLevel>>> =
		settings.streamNotificationTriggerLevels()
			.observeOn(AndroidSchedulers.mainThread())

	fun onTriggerLevelSelected(place: Place, triggerLevel: TriggerLevel) {
		disposables += settings.setNotificationTriggerLevel(place, triggerLevel).subscribe()
	}

	override fun onCleared() = disposables.clear()
}
