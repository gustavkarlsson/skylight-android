package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds

@Module
object LibLocationModule {

    @Provides
    @AppScope
    internal fun locationProvider(context: Context): LocationProvider =
        RxLocationLocationProvider(
            fusedLocation = RxLocation(context).location(),
            timeout = 30.seconds,
            requestAccuracy = LocationRequest.PRIORITY_HIGH_ACCURACY,
            throttleDuration = 1.minutes,
            firstPollingInterval = 10.seconds,
            restPollingInterval = 10.minutes,
            retryDelay = 30.seconds
        )
}
