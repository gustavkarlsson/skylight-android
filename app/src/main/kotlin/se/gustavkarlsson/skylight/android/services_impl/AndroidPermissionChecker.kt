package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.services.PermissionChecker

class AndroidPermissionChecker(private val context: Context) :
	PermissionChecker {

	override val isLocationGranted: Boolean
		get() {
			val result =
				ContextCompat.checkSelfPermission(context, appComponent.locationPermission)
			return result == PackageManager.PERMISSION_GRANTED
		}
}
