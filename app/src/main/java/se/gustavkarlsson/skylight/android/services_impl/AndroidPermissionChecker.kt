package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import timber.log.Timber

class AndroidPermissionChecker(
	private val context: Context,
	private val permissionKey: String
) : PermissionChecker {

	private val permissionRelay = BehaviorRelay.createDefault(checkSystemPermission())

	private fun checkSystemPermission(): Permission {
		val result = ContextCompat.checkSelfPermission(context, permissionKey)
		val permission = if (result == PackageManager.PERMISSION_GRANTED)
			Permission.Granted
		else
			Permission.Denied
		Timber.d("$permissionKey = $permission")
		return permission
	}

	override val permission: Flowable<Permission> =
		permissionRelay
			.distinctUntilChanged()
			.toFlowable(BackpressureStrategy.LATEST)

	override fun signalDeniedForever() {
		permissionRelay.accept(Permission.DeniedForever)
	}

	override fun refresh() {
		val systemPermission = checkSystemPermission()
		if (systemPermission == Permission.Denied && permissionRelay.value == Permission.DeniedForever) {
			Timber.d("Won't change from ${Permission.DeniedForever} to ${Permission.Denied}")
			return
		}
		permissionRelay.accept(systemPermission)
	}
}
