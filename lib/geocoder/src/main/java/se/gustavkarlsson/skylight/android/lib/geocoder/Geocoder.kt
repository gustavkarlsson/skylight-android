package se.gustavkarlsson.skylight.android.lib.geocoder

interface Geocoder {
    suspend fun geocode(locationName: String): GeocodingResult
}
