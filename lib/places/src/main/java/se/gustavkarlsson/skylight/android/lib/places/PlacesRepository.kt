package se.gustavkarlsson.skylight.android.lib.places

import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.lib.location.Location

interface PlacesRepository {
    // FIXME Don't use this function to limit number of places to keep
    suspend fun insert(name: String, location: Location): Place.Saved
    suspend fun updateLastChanged(placeId: PlaceId.Saved): Place.Saved
    fun stream(): Flow<NonEmptyList<Place>>
}
