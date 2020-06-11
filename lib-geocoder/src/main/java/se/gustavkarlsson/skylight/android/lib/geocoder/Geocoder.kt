package se.gustavkarlsson.skylight.android.lib.geocoder

import io.reactivex.Single

interface Geocoder {
    fun geocode(locationName: String): Single<GeocodingResult>
}
