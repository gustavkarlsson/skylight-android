package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import se.gustavkarlsson.skylight.android.core.Main
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import javax.inject.Inject

internal class GooglePlayServicesViewModel @Inject constructor(
    @Main private val dispatcher: CoroutineDispatcher,
) : ScopedService {
    // TODO Extract to library module (together with GooglePlayServicesChecker)
    suspend fun makeGooglePlayServicesAvailable(activity: Activity) {
        withContext(dispatcher + CoroutineName("makeGooglePlayServicesAvailable")) {
            GoogleApiAvailability.getInstance()
                .makeGooglePlayServicesAvailable(activity)
                .await()
        }
    }
}
