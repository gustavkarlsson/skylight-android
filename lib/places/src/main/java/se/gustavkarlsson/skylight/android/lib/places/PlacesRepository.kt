package se.gustavkarlsson.skylight.android.lib.places

import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.lib.location.Location

interface PlacesRepository {
    fun add(name: String, location: Location)
    fun remove(placeId: Long)
    fun stream(): Observable<List<Place>>
}
