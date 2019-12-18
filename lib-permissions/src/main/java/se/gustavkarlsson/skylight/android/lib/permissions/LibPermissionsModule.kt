package se.gustavkarlsson.skylight.android.lib.permissions

import android.Manifest
import androidx.fragment.app.FragmentActivity
import com.jakewharton.rxrelay2.BehaviorRelay
import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.PermissionRequester

val libPermissionsModule = module {

	val relay = BehaviorRelay.create<Permission>()

	single("locationPermission") {
		Manifest.permission.ACCESS_FINE_LOCATION
	}

	single<PermissionChecker> {
		AndroidPermissionChecker(
			permissionKey = get("locationPermission"),
			permissionRelay = relay,
			context = get()
		)
	}

	scope<PermissionRequester>("activity") {
		val activity = get<FragmentActivity>(scopeId = "activity")
		val rxPermissions = RxPermissions(activity)
			.apply { setLogging(BuildConfig.DEBUG) }
		RxPermissionRequester(
			permissionKey = get("locationPermission"),
			rxPermissions = rxPermissions,
			permissionChangeConsumer = relay
		)
	}
}
