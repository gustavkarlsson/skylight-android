package se.gustavkarlsson.skylight.android.services_impl.providers

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.services.providers.PermissionProvider

class AndroidPermissionProvider(private val context: Context) : PermissionProvider {

	override val isLocationGranted: Boolean
		get() {
			val result =
				ContextCompat.checkSelfPermission(context, appComponent.locationPermission)
			return result == PackageManager.PERMISSION_GRANTED
		}
}
