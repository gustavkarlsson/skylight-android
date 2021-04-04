package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface ReverseGeocoder {
    suspend fun get(locationResult: LocationResult): ReverseGeocodingResult
    fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<ReverseGeocodingResult>>
}
