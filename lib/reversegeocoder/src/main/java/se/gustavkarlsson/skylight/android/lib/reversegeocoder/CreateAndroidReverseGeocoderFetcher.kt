package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import androidx.annotation.RequiresApi
import arrow.core.Option
import arrow.core.toOption
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal fun createAndroidReverseGeocoderFetcher(
    getGeocoder: () -> Geocoder,
    dispatcher: CoroutineDispatcher,
): Fetcher<ApproximatedLocation, Option<String>> = Fetcher.ofResult { location ->
    withContext(dispatcher + CoroutineName("reverseGeocoderFetcher")) {
        val geocoder = getGeocoder()
        try {
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location, 10)
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 10) ?: emptyList()
            }
            val bestName = addresses.getBestName()
            logInfo { "Reverse geocoded $location to $bestName" }
            FetcherResult.Data(bestName.toOption())
        } catch (e: IOException) {
            logWarn(e) { "Failed to reverse geocode: $location" }
            FetcherResult.Error.Exception(e)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private suspend fun Geocoder.getFromLocation(location: ApproximatedLocation, maxResults: Int): List<Address> {
    return suspendCoroutine { continuation ->
        val listener = object : GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                continuation.resume(addresses)
            }

            override fun onError(errorMessage: String?) {
                continuation.resumeWithException(RuntimeException(errorMessage ?: "Unknown geocoding error"))
            }
        }
        getFromLocation(location.latitude, location.longitude, maxResults, listener)
    }
}

private fun List<Address>.getBestName() = firstNotNullOfOrNull { it.locality ?: it.subAdminArea ?: it.adminArea }
