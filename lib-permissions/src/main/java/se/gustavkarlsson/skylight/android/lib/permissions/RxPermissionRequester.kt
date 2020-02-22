package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.Fragment
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.entities.Access
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.services.PermissionRequester
import timber.log.Timber

internal class RxPermissionRequester<T : Permission>(
    permission: T,
    private val accessChangeConsumer: Consumer<Access>
) : PermissionRequester<T> {

    private val permissionKey = permission.key

    override fun request(fragment: Fragment): Completable {
        val rxPermissions = RxPermissions(fragment)
            .apply { setLogging(BuildConfig.DEBUG) }

        return rxPermissions.requestEach(permissionKey)
            .doOnNext {
                when {
                    it.granted -> {
                        Timber.i("Permission is granted")
                        accessChangeConsumer.accept(Access.Granted)
                    }
                    it.shouldShowRequestPermissionRationale -> {
                        Timber.i("Permission is denied")
                        accessChangeConsumer.accept(Access.Denied)
                    }
                    else -> {
                        Timber.i("Permission is denied forever")
                        accessChangeConsumer.accept(Access.DeniedForever)
                    }
                }
            }
            .ignoreElements()
    }
}
