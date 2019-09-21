package se.gustavkarlsson.skylight.android.lib.places

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location

interface PlacesRepository {
	fun add(name: String, location: Location)
	fun remove(placeId: Long)
	val all: Flowable<List<Place>>
}
