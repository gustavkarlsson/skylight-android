package se.gustavkarlsson.skylight.android.lib.geocoder

import se.gustavkarlsson.skylight.android.lib.location.Location

interface Geocoder {
    suspend fun geocode(locationName: String, biasAround: Location? = null): GeocodingResult
}
