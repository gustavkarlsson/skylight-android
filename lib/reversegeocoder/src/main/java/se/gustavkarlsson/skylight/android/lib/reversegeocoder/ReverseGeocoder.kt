package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface ReverseGeocoder {
    suspend fun get(location: LocationResult, fresh: Boolean = false): ReverseGeocodingResult
    fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<ReverseGeocodingResult>>
}
