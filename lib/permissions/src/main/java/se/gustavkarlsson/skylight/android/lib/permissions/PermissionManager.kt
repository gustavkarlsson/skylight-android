package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import javax.inject.Inject

@AppScope
internal class PermissionManager @Inject constructor(
    private val context: Context,
) : PermissionChecker, PermissionRequester {

    private val state = MutableStateFlow(Permissions(getPermissions()))

    override val permissions: StateFlow<Permissions> get() = state

    override fun refresh() {
        val new = Permissions(getPermissions())
        val old = state.value
        state.value = new
        logInfo { "Permissions changed from $old to $new" }
    }

    private fun getPermissions(): Map<Permission, Access> {
        return Permission.values().map { permission ->
            val access = getAccess(permission)
            permission to access
        }.toMap()
    }

    private fun getAccess(permission: Permission): Access {
        val result = ContextCompat.checkSelfPermission(context, permission.key)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            Access.Granted
        } else Access.Denied
    }

    override suspend fun request(activity: FragmentActivity, permission: Permission) {
        logInfo { "Requesting permission for $permission using key '${permission.key}'" }

        // The implementation of this lib is a bit wonky. askPermission returns a result,
        // but throws whenever the result is not granted. Hence the catch
        val newAccess = try {
            activity.askPermission(permission.key)
            Access.Granted
        } catch (e: PermissionException) {
            when (permission.key) {
                in e.foreverDenied -> Access.DeniedForever
                in e.denied -> Access.Denied
                else -> throw IllegalStateException("Unexpected state for key: ${permission.key}", e)
            }
        }
        val old = state.value
        val new = old.update(permission, newAccess)
        state.value = new
    }
}
