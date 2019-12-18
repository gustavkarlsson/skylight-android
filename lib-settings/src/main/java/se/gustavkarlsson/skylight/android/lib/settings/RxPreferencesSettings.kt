package se.gustavkarlsson.skylight.android.lib.settings

import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.jakewharton.rx.replayingShare
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.rxkotlin.combineLatest
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.Settings

internal class RxPreferencesSettings(
	private val rxSharedPreferences: RxSharedPreferences,
	placeRepository: PlacesRepository
) : Settings {

	private val defaultTriggerLevel =
		ChanceLevel.values().indexOf(Settings.DEFAULT_TRIGGER_LEVEL).toString()

	override val notificationTriggerLevels: Flowable<List<Pair<Place, ChanceLevel?>>> =
		placeRepository.all
			.switchMap { places ->
				places.map { place ->
					getTriggerLevelFlowable(place)
						.map { place to it.value }
				}.combineLatest { it }
			}
			.replayingShare()

	private fun getTriggerLevelFlowable(place: Place): Flowable<Optional<ChanceLevel>> =
		rxSharedPreferences
			.getString(getTriggerLevelPreferenceKey(place), defaultTriggerLevel)
			.asObservable()
			.toFlowable(BackpressureStrategy.LATEST)
			.map {
				val ordinal = it.toInt()
				val chanceLevel =
					if (ordinal == -1) null
					else ChanceLevel.values()[ordinal]
				chanceLevel.toOptional()
			}

	private fun getTriggerLevelPreferenceKey(place: Place) =
		when (place) {
			Place.Current -> "pref_notifications_place_key_current"
			is Place.Custom -> "pref_notifications_place_key_${place.id}"
		}
}
