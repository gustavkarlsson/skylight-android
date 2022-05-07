package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import arrow.core.NonEmptyList
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
        val latest = getPermissions()
        val new = latest.update(permission, newAccess)
        state.value = new
    }

    // The implementation of this lib is a bit wonky. askPermission returns a result,
    // but throws whenever the result is not granted. Hence the catch
    private suspend fun doRequest(activity: FragmentActivity, keys: NonEmptyList<String>): Access = try {
        activity.askPermission(*keys.toTypedArray())
        Access.Granted
    } catch (e: PermissionException) {
        when {
            keys.any { it in e.accepted } -> Access.Granted
            keys.any { it in e.denied } -> Access.Denied
            keys.any { it in e.foreverDenied } -> Access.DeniedForever
            else -> throw IllegalStateException("Unexpected state for keys: $keys", e) // FIXME change permission lib?
        }
    }
}
