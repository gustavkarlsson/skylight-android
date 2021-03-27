package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.flow.Flow
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.lib.location.Location

interface PlacesRepository {
    suspend fun addFavorite(name: String, location: Location): Place.Favorite
    suspend fun addRecent(name: String, location: Location): Place.Recent
    suspend fun setLastChanged(placeId: Long, time: Instant)
    suspend fun remove(placeId: Long)
    fun stream(): Flow<List<Place>>
}
