package se.gustavkarlsson.skylight.android.lib.geocoder

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.location.Location
import java.io.IOException
import java.util.Locale

internal class MapboxGeocoder(
    private val accessToken: String,
    private val getLocale: () -> Locale
) : Geocoder {

    override fun geocode(locationName: String): Single<GeocodingResult> {
        return if (locationName.length <= 1) {
            Single.just(GeocodingResult.Success(emptyList()))
        } else {
            createSingle(accessToken, getLocale(), locationName)
                .subscribeOn(Schedulers.io())
        }
    }
}

private fun createSingle(
    accessToken: String,
    locale: Locale,
    locationName: String
) =
    Single.create<GeocodingResult> { emitter ->
        val geocoding = try {
            createGeocoding(accessToken, locale, locationName)
        } catch (e: Exception) {
            logError(e) { "Failed to create Geocoding request" }
            emitter.onSuccess(GeocodingResult.Failure.Unknown)
            return@create
        }

        emitter.setCancellable(geocoding::cancelCall)

        geocoding.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                val result = if (t is IOException) {
                    logWarn(t) { "Geocoding failed" }
                    GeocodingResult.Failure.Io
                } else {
                    logError(t) { "Geocoding failed" }
                    GeocodingResult.Failure.Unknown
                }
                emitter.onSuccess(result)
            }

            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: Response<GeocodingResponse>
            ) {
                if (response.isSuccessful) {
                    emitter.onSuccess(response.body()!!.toGeocodingResultSuccess())
                } else {
                    val code = response.code()
                    val error = response.errorBody()?.string() ?: "<empty>"
                    logError { "Geocoding failed with HTTP $code: $error" }
                    emitter.onSuccess(GeocodingResult.Failure.ServerError)
                }
            }
        })
    }

// TODO Add proximity bias
private fun createGeocoding(
    accessToken: String,
    locale: Locale,
    locationName: String
) = MapboxGeocoding.builder()
    .accessToken(accessToken)
    .languages(locale)
    .limit(10)
    .autocomplete(true)
    .query(locationName)
    .build()

private fun GeocodingResponse.toGeocodingResultSuccess(): GeocodingResult.Success {
    val suggestions = features().mapNotNull { feature ->
        val center = feature.center()
        val fullName = feature.placeName() ?: feature.text()
        val simpleName = feature.text() ?: feature.placeName()
        if (center == null || fullName == null || simpleName == null)
            null
        else
            PlaceSuggestion(
                Location(center.latitude(), center.longitude()),
                fullName,
                simpleName
            )
    }
    return GeocodingResult.Success(suggestions)
}
