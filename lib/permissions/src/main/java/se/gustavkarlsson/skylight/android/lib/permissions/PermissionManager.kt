package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import arrow.core.NonEmptyList
import com.afollestad.assent.coroutines.awaitPermissionsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import javax.inject.Inject
import com.afollestad.assent.Permission.Companion as AssentPermission

@AppScope
internal class PermissionManager @Inject constructor(
    private val context: Context,
) : PermissionChecker, PermissionRequester {

    private val state = MutableStateFlow(getPermissions())

    override val permissions: StateFlow<Permissions> = state

    override fun refresh() {
        val new = getPermissions()
        val old = state.value
        state.value = new
        logInfo { "Permissions changed from $old to $new" }
    }

    private fun getPermissions(): Permissions {
        val map = Permission.values().associate { permission ->
            val access = getAccess(permission)
            permission to access
        }
        return Permissions(map)
    }

    private fun getAccess(permission: Permission): Access {
        val result = ContextCompat.checkSelfPermission(context, permission.checkKey)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            Access.Granted
        } else Access.Denied
    }

    override suspend fun request(activity: FragmentActivity, permission: Permission) {
        logInfo { "Requesting permission for $permission" }
        val newAccess = doRequest(activity, permission.requestKeys)
        logInfo { "New permission for $permission set to $newAccess" }
        val latest = getPermissions()
        val new = latest.update(permission, newAccess)
        state.value = new
        logInfo { "Permissions changed from $latest to $new" }
    }

    private suspend fun doRequest(activity: FragmentActivity, keys: NonEmptyList<String>): Access {
        val assentPermissions = keys.map { AssentPermission.parse(it) }.toTypedArray()
        val result = activity.awaitPermissionsResult(*assentPermissions)
        return when {
            assentPermissions.any { it in result.granted() } -> Access.Granted
            assentPermissions.any { it in result.permanentlyDenied() } -> Access.DeniedForever
            assentPermissions.any { it in result.denied() } -> Access.Denied
            else -> throw IllegalStateException("Unexpected result: $result for keys: $keys")
        }
    }
}
