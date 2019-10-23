package se.gustavkarlsson.skylight.android.lib.permissions

import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.services.PermissionRequester
import timber.log.Timber

internal class RxPermissionRequester(
	private val permissionKey: String,
	private val rxPermissions: RxPermissions,
	private val permissionChangeConsumer: Consumer<Permission>

) : PermissionRequester {
	override fun request(): Completable =
		rxPermissions.requestEach(permissionKey)
			.doOnNext {
				when {
					it.granted -> {
						Timber.i("Permission is granted")
						permissionChangeConsumer.accept(Permission.Granted)
					}
					it.shouldShowRequestPermissionRationale -> {
						Timber.i("Permission is denied")
						permissionChangeConsumer.accept(Permission.Denied)
					}
					else -> {
						Timber.i("Permission is denied forever")
						permissionChangeConsumer.accept(Permission.DeniedForever)
					}
				}
			}
			.ignoreElements()
}
