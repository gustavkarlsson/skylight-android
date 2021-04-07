package se.gustavkarlsson.skylight.android.lib.geocoder

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.location.Location

internal class MapboxGeocoder(
    private val accessToken: String,
    private val getLocale: () -> Locale,
    private val dispatcher: CoroutineDispatcher
) : Geocoder {

    override suspend fun geocode(locationName: String, biasAround: Location?): GeocodingResult {
        if (locationName.isBlank()) {
            return GeocodingResult.Success(emptyList())
        }
        return withContext(dispatcher + CoroutineName("geocode")) {
            try {
                val geocoding = createGeocoding(accessToken, getLocale(), locationName, biasAround)
                doGeocode(geocoding)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logError(e) { "Failed to create Geocoding request" }
                GeocodingResult.Failure.Unknown
            }
        }
    }

    private suspend fun doGeocode(geocoding: MapboxGeocoding): GeocodingResult =
        suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { geocoding.cancelCall() }
            val call = ContinuationCallback(continuation)
            geocoding.enqueueCall(call)
        }
}

private class ContinuationCallback(
    private val continuation: CancellableContinuation<GeocodingResult>,
) : Callback<GeocodingResponse> {
    override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
        if (response.isSuccessful) {
            val result = response.body()!!.toGeocodingResultSuccess()
            continuation.resume(result)
        } else {
            val code = response.code()
            val error = response.errorBody()?.string() ?: "<empty>"
            logError { "Geocoding failed with HTTP $code: $error" }
            continuation.resume(GeocodingResult.Failure.Server)
        }
    }

    override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
        val result = if (t is IOException) {
            logWarn(t) { "Geocoding failed" }
            GeocodingResult.Failure.Io
        } else {
            logError(t) { "Geocoding failed" }
            GeocodingResult.Failure.Unknown
        }
        continuation.resume(result)
    }
}

private fun createGeocoding(
    accessToken: String,
    locale: Locale,
    locationName: String,
    biasAround: Location?,
) = MapboxGeocoding.builder()
    .accessToken(accessToken)
    .languages(locale)
    .limit(10)
    .autocomplete(true)
    .query(locationName)
    .apply {
        if (biasAround != null) {
            val point = Point.fromLngLat(biasAround.longitude, biasAround.latitude)
            proximity(point)
        }
    }
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
