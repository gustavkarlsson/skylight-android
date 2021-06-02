package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import android.os.Looper
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import kotlin.time.ExperimentalTime

@Module
object LibLocationModule {

    @OptIn(
        FlowPreview::class,
        ExperimentalCoroutinesApi::class,
        ExperimentalTime::class,
    )
    @Provides
    @AppScope
    internal fun locationProvider(
        context: Context,
        @Io dispatcher: CoroutineDispatcher
    ): LocationProvider {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val requestInterval = 10.minutes
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            fastestInterval = 1.minutes.toMillis()
            interval = requestInterval.toMillis()
            maxWaitTime = 10.minutes.toMillis()
            smallestDisplacement = 1000.toFloat()
        }
        val fetcher = createLocationFetcher(
            client = client,
            locationRequest = locationRequest,
            looper = Looper.getMainLooper(),
            retryDelay = 15.seconds,
            dispatcher = dispatcher,
        )
        val expiry = Duration.milliseconds(requestInterval.toMillis() / 2)
        val cachePolicy = MemoryPolicy.builder<Unit, LocationResult>()
            .setExpireAfterWrite(expiry)
            .build()
        val store = StoreBuilder.from(fetcher)
            .cachePolicy(cachePolicy)
            .build()
        return StoreLocationProvider(store)
    }
}
