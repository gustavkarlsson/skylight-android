package se.gustavkarlsson.skylight.android.services

import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Place

interface SelectedPlaceRepository {
	fun set(place: Place)
	val selected: Observable<Place>
}
