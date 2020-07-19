package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.Fragment
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class RxPermissionRequester(
    private val permissionKeys: List<String>,
    private val accessChangeConsumer: Consumer<Access>
) : PermissionRequester {

    override fun request(fragment: Fragment): Completable {
        val rxPermissions = RxPermissions(fragment)
            .apply { setLogging(BuildConfig.DEBUG) }

        return rxPermissions.requestEach(*permissionKeys.toTypedArray())
            .doOnNext {
                when {
                    it.granted -> {
                        logInfo { "Permission is granted" }
                        accessChangeConsumer.accept(Access.Granted)
                    }
                    it.shouldShowRequestPermissionRationale -> {
                        logInfo { "Permission is denied" }
                        accessChangeConsumer.accept(Access.Denied)
                    }
                    else -> {
                        logInfo { "Permission is denied forever" }
                        accessChangeConsumer.accept(Access.DeniedForever)
                    }
                }
            }
            .ignoreElements()
    }
}
