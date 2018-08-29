package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

class GmsGooglePlayServicesChecker(private val context: Context) :
	GooglePlayServicesChecker {

	override val isAvailable: Boolean
		get() {
			val result =
				GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
			return result == ConnectionResult.SUCCESS
		}

}
