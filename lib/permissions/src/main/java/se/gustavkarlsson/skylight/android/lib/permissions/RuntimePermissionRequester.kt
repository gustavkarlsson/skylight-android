package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.FragmentActivity
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class RuntimePermissionRequester(
    private val requiredPermissionKeys: List<String>,
    private val extraPermissionKeys: List<String>,
    private val onNewAccess: (Access) -> Unit
) : PermissionRequester {

    override suspend fun request(activity: FragmentActivity) {
        val allKeys = requiredPermissionKeys + extraPermissionKeys

        // The implementation of this lib is a bit wonky. askPermission returns a result,
        // but throws whenever the result is not granted. Hence the catch
        val access = try {
            activity.askPermission(*allKeys.toTypedArray())
            Access.Granted
        } catch (e: PermissionException) {
            when {
                e.accepted.containsAll(requiredPermissionKeys) -> Access.Granted
                e.foreverDenied.any { it in requiredPermissionKeys } -> Access.DeniedForever
                e.denied.any { it in requiredPermissionKeys } -> Access.Denied
                else -> error("Unexpected state: $e")
            }
        }
        logInfo { "Permission is $access" }
        onNewAccess(access)
    }
}
