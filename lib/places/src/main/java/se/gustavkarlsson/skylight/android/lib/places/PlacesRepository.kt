package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.lib.location.Location

interface PlacesRepository {
    suspend fun setFavorite(place: Place.Saved): Place.Saved.Favorite
    suspend fun setRecent(place: Place.Saved): Place.Saved.Recent
    suspend fun addRecent(name: String, location: Location): Place.Saved.Recent
    suspend fun updateLastChanged(place: Place.Saved): Place
    fun stream(): Flow<List<Place>>
}
