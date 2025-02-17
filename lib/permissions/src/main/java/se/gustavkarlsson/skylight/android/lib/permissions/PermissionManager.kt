package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import arrow.core.NonEmptyList
import com.afollestad.assent.coroutines.awaitPermissionsResult
import com.afollestad.assent.toPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.logging.logInfo

@Inject
@PermissionsScope
internal class PermissionManager(
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
        val map = Permission.entries.associateWith { permission ->
            getAccess(permission)
        }
        return Permissions(map)
    }

    private fun getAccess(permission: Permission): Access {
        val result = ContextCompat.checkSelfPermission(context, permission.checkKey)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            Access.Granted
        } else {
            Access.Denied
        }
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
        val assentPermissions = keys.map { it.toPermission() }.toTypedArray()
        val result = activity.awaitPermissionsResult(*assentPermissions)
        return when {
            assentPermissions.any { it in result.granted() } -> Access.Granted
            assentPermissions.any { it in result.permanentlyDenied() } -> Access.DeniedForever
            assentPermissions.any { it in result.denied() } -> Access.Denied
            else -> throw IllegalStateException("Unexpected result: $result for keys: $keys")
        }
    }
}
