package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

internal class GmsGooglePlayServicesChecker(private val context: Context) :
    GooglePlayServicesChecker {

    override val isAvailable: Boolean
        get() {
            val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            return result == ConnectionResult.SUCCESS
        }
}
