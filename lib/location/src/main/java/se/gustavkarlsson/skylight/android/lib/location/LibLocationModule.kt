package se.gustavkarlsson.skylight.android.lib.location

import android.content.Context
import com.google.android.gms.location.LocationRequest
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds

@Module
object LibLocationModule {

    @Provides
    @AppScope
    internal fun locationProvider(
        context: Context,
        @Io dispatcher: CoroutineDispatcher
    ): LocationProvider =
        FusedLocationProviderProvider(
            context = context,
            requestAccuracy = LocationRequest.PRIORITY_HIGH_ACCURACY,
            requestInterval = 10.minutes,
            timeout = 30.seconds,
            retryDelay = 15.seconds,
            dispatcher = dispatcher
        )
}
