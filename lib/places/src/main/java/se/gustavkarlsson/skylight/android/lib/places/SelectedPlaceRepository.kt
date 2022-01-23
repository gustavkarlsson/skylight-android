package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.flow.Flow

interface SelectedPlaceRepository {
    suspend fun set(placeId: PlaceId)
    fun stream(): Flow<Place>
}
