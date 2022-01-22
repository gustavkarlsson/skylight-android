package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject

internal class GmsGooglePlayServicesChecker @Inject constructor(
    private val context: Context,
) : GooglePlayServicesChecker {

    override val isAvailable: Boolean
        get() {
            val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            return result == ConnectionResult.SUCCESS
        }
}
