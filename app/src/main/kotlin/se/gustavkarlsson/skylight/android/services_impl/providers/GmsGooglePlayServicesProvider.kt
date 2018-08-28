package se.gustavkarlsson.skylight.android.services_impl.providers

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import se.gustavkarlsson.skylight.android.services.providers.GooglePlayServicesProvider

class GmsGooglePlayServicesProvider(private val context: Context) : GooglePlayServicesProvider {

	override val isAvailable: Boolean
		get() {
			val result =
				GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
			return result == ConnectionResult.SUCCESS
		}

}
