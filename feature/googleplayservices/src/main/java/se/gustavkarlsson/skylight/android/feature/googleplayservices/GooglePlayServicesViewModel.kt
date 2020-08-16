package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import com.google.android.gms.common.GoogleApiAvailability
import io.ashdavies.rx.rxtasks.toCompletable
import io.reactivex.Completable
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import javax.inject.Inject

internal class GooglePlayServicesViewModel @Inject constructor() : ScopedService {
    // TODO Extract to library module (together with GooglePlayServicesChecker)
    suspend fun makeGooglePlayServicesAvailable(activity: Activity) {
        withContext(Dispatchers.Main + CoroutineName("makeGooglePlayServicesAvailable")) {
            GoogleApiAvailability.getInstance()
                .makeGooglePlayServicesAvailable(activity)
                .await()
        }
    }
}
