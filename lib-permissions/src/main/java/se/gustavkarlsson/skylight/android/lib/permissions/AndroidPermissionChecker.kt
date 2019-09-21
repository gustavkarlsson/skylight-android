package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.jakewharton.rxrelay2.BehaviorRelay
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Permission
import timber.log.Timber

internal class AndroidPermissionChecker(
	private val permissionKey: String,
	private val context: Context,
	private val permissionRelay: BehaviorRelay<Permission>
) : PermissionChecker {

	override val permission: Flowable<Permission> =
		permissionRelay
			.distinctUntilChanged()
			.toFlowable(BackpressureStrategy.LATEST)
			.doOnSubscribe { refresh() }

	override fun refresh() {
		val systemPermission = checkSystemPermission()
		if (systemPermission == Permission.Denied && permissionRelay.value == Permission.DeniedForever) {
			Timber.d("Won't change from ${Permission.DeniedForever} to ${Permission.Denied}")
			return
		}
		permissionRelay.accept(systemPermission)
	}

	private fun checkSystemPermission(): Permission {
		val result = ContextCompat.checkSelfPermission(context, permissionKey)
		val permission = if (result == PackageManager.PERMISSION_GRANTED)
			Permission.Granted
		else
			Permission.Denied
		Timber.d("$permissionKey = $permission")
		return permission
	}
}
