package se.gustavkarlsson.skylight.android.lib.places

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Place

interface PlacesRepository {
	fun add(name: String, location: Location)
	fun remove(placeId: Long)
	fun all(): Flowable<List<Place>>
}
