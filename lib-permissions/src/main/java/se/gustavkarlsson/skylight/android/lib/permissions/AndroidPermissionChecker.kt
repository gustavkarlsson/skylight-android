package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import timber.log.Timber

internal class AndroidPermissionChecker(
    private val permissionKey: String,
    private val context: Context,
    private val permissionRelay: BehaviorRelay<Permission>
) : PermissionChecker {

    override val permission: Observable<Permission> =
        permissionRelay
            .distinctUntilChanged()
            .doOnSubscribe { refresh() }

    override fun refresh() {
        val systemPermission = checkSystemPermission()
        if (systemPermission == Permission.Denied && permissionRelay.value == Permission.DeniedForever) {
            Timber.d("Won't change from %s to %s", Permission.DeniedForever, Permission.Denied)
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
        Timber.d("%s = %s", permissionKey, permission)
        return permission
    }
}
