package se.gustavkarlsson.skylight.android.services

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeocodingResult

interface Geocoder {
    fun geocode(locationName: String): Single<GeocodingResult>
}
