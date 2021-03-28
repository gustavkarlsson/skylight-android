package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.lib.location.Location

interface PlacesRepository {
    suspend fun setFavorite(placeId: Long): Place.Favorite
    suspend fun setRecent(placeId: Long): Place.Recent
    suspend fun addRecent(name: String, location: Location): Place.Recent
    fun stream(): Flow<List<Place>>
}
