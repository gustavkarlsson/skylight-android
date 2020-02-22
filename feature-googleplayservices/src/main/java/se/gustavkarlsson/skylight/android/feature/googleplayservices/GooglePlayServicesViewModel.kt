package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.GoogleApiAvailability
import io.ashdavies.rx.rxtasks.toCompletable
import io.reactivex.Completable

internal class GooglePlayServicesViewModel : ViewModel() {
    // TODO Extract to library module (together with GooglePlayServicesChecker)
    fun makeGooglePlayServicesAvailable(activity: Activity): Completable =
        GoogleApiAvailability.getInstance()
            .makeGooglePlayServicesAvailable(activity).toCompletable()
}
