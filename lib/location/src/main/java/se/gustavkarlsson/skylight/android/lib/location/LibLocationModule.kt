package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@Module
object LibLocationModule {

    @FlowPreview
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @Provides
    @AppScope
    internal fun locationProvider(
        context: Context,
        @Io dispatcher: CoroutineDispatcher
    ): LocationProvider {
        val client = FusedLocationProviderClient(context)
        val requestInterval = 10.minutes
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            fastestInterval = 1.minutes.toMillis()
            interval = requestInterval.toMillis()
            maxWaitTime = 10.minutes.toMillis()
            smallestDisplacement = 1000.toFloat()
        }
        val fetcher = createLocationFetcher(
            client = client,
            locationRequest = locationRequest,
            retryDelay = 15.seconds,
            dispatcher = dispatcher,
        )
        val expiry = (requestInterval.toMillis() / 2).milliseconds
        val cachePolicy = MemoryPolicy.builder<Unit, LocationResult>()
            .setExpireAfterWrite(expiry)
            .build()
        val store = StoreBuilder.from(fetcher)
            .cachePolicy(cachePolicy)
            .build()
        return StoreLocationProvider(store)
    }
}
