package se.gustavkarlsson.skylight.android.lib.places

import io.reactivex.Observable

interface SelectedPlaceRepository {
	fun set(place: Place)
	val selected: Observable<Place>
}
