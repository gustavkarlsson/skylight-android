package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.location.Address
import android.location.Geocoder
import arrow.core.Option
import arrow.core.toOption
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.FetcherResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import java.io.IOException

internal fun createAndroidReverseGeocoderFetcher(
    geocoder: Geocoder,
    dispatcher: CoroutineDispatcher
): Fetcher<ApproximatedLocation, Option<String>> = Fetcher.ofResult { location ->
    withContext(dispatcher + CoroutineName("reverseGeocoderFetcher")) {
        try {
            @Suppress("BlockingMethodInNonBlockingContext")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 10)
            val bestName = addresses.getBestName()
            logInfo { "Reverse geocoded $location to $bestName" }
            FetcherResult.Data(bestName.toOption())
        } catch (e: IOException) {
            logWarn(e) { "Failed to reverse geocode: $location" }
            FetcherResult.Error.Exception(e)
        }
    }
}

private fun List<Address>.getBestName() =
    mapNotNull { it.locality ?: it.subAdminArea ?: it.adminArea }.firstOrNull()
