package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import se.gustavkarlsson.skylight.android.services.PermissionChecker

class AndroidPermissionChecker(
	private val context: Context,
	private val permission: String
) : PermissionChecker {

	override val isLocationGranted: Boolean
		get() {
			val result = ContextCompat.checkSelfPermission(context, permission)
			return result == PackageManager.PERMISSION_GRANTED
		}
}
