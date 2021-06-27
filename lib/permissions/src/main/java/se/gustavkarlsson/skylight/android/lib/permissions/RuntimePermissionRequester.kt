package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.FragmentActivity
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import kotlinx.coroutines.flow.MutableStateFlow
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class RuntimePermissionRequester(
    private val state: MutableStateFlow<Permissions>,
) : PermissionRequester {

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
