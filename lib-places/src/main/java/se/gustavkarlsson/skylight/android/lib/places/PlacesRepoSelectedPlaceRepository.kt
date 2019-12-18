package se.gustavkarlsson.skylight.android.lib.places

import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.extensions.mapNotNull
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository
import timber.log.Timber

internal class PlacesRepoSelectedPlaceRepository(
	placesRepo: PlacesRepository
) : SelectedPlaceRepository {
	private val selectedPlaceRelay = PublishRelay.create<Place>()

	override fun set(place: Place) = selectedPlaceRelay.accept(place)

	private val autoSelectedPlace = placesRepo.all
		.toObservable()
		.buffer(2, 1)
		.mapNotNull { buffer ->
			when {
				buffer.size < 2 -> null
				buffer[0].isEmpty() -> null
				buffer[1].size > buffer[0].size -> (buffer[1] - buffer[0]).first()
				else -> null
			}
		}

	private val selectedPlace = Observable.merge(selectedPlaceRelay, autoSelectedPlace)

	override val selected: Observable<Place> =
		Observables
			.combineLatest(
				placesRepo.all.toObservable(),
				selectedPlace
			) { places, selected ->
				if (selected in places) selected
				else places.first()
			}
			.distinctUntilChanged()
			.replayingShare(Place.Current) // TODO get persisted
			.doOnNext {
				// TODO Persist
			}
}
