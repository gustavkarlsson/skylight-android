package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import com.google.android.gms.common.GoogleApiAvailability
import io.ashdavies.rx.rxtasks.toCompletable
import io.reactivex.Completable
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService

internal class GooglePlayServicesViewModel :
    ScopedService {
    // TODO Extract to library module (together with GooglePlayServicesChecker)
    fun makeGooglePlayServicesAvailable(activity: Activity): Completable =
        GoogleApiAvailability.getInstance()
            .makeGooglePlayServicesAvailable(activity).toCompletable()
}
