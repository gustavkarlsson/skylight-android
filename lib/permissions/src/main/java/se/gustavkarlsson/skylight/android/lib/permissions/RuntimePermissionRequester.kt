package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.FragmentActivity
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class RuntimePermissionRequester(
    private val onNewAccesses: (List<PermissionAccess>) -> Unit,
) : PermissionRequester {

    override suspend fun request(activity: FragmentActivity, vararg permissions: Permission) {
        if (permissions.isEmpty()) return
        val requested = permissions.toSet()
        logInfo { "Requesting permission for $requested" }
        logUnsupported(requested)
        val keys = requested.intersect(Permission.supported)
            .map { permission -> permission.key }

        // The implementation of this lib is a bit wonky. askPermission returns a result,
        // but throws whenever the result is not granted. Hence the catch
        val permissionAccesses = try {
            activity.askPermission(*keys.toTypedArray())
            keys.map { key -> PermissionAccess(Permission.fromKey(key), Access.Granted) }
        } catch (e: PermissionException) {
            keys
                .mapNotNull { key ->
                    val access = when (key) {
                        in e.accepted -> Access.Granted
                        in e.foreverDenied -> Access.DeniedForever
                        in e.denied -> Access.Denied
                        else -> {
                            logError { "Unexpected state for key: $key: $e" }
                            return@mapNotNull null
                        }
                    }
                    PermissionAccess(Permission.fromKey(key), access)
                }
        }
        logInfo { "Permissions are $permissionAccesses" }
        onNewAccesses(permissionAccesses)
    }

    private fun logUnsupported(requested: Set<Permission>) {
        val unsupported = requested subtract Permission.supported
        if (unsupported.isNotEmpty()) {
            logInfo { "Skipped unsupported permissions: $unsupported" }
        }
    }
}
