package se.gustavkarlsson.skylight.android.lib.geocoder

import se.gustavkarlsson.skylight.android.lib.location.Location

interface Geocoder {
    // TODO Call with bias?
    suspend fun geocode(locationName: String, biasAround: Location? = null): GeocodingResult
}
