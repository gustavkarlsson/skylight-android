package se.gustavkarlsson.skylight.android.lib.places

import kotlinx.coroutines.flow.Flow

interface SelectedPlaceRepository {
    fun set(place: Place)
    fun stream(): Flow<Place>
}
