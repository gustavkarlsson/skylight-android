package se.gustavkarlsson.skylight.android.lib.places

import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.lib.location.Location

interface PlacesRepository {
    suspend fun setFavorite(placeId: PlaceId.Saved): Place.Saved.Favorite
    suspend fun setRecent(placeId: PlaceId.Saved): Place.Saved.Recent
    suspend fun addRecent(name: String, location: Location): Place.Saved.Recent
    suspend fun updateLastChanged(placeId: PlaceId.Saved): Place
    fun stream(): Flow<NonEmptyList<Place>>
}
