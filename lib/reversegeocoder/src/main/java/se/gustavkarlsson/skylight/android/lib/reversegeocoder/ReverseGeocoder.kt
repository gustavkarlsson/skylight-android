package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.Location

interface ReverseGeocoder {
    suspend fun get(location: Location): ReverseGeocodingResult
    fun stream(location: Location): Flow<Loadable<ReverseGeocodingResult>>
}
