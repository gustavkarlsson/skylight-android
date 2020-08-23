package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.lib.location.Location

interface PlacesRepository {
    fun add(name: String, location: Location)
    fun remove(placeId: Long)
    fun stream(): Flow<List<Place>>
}
