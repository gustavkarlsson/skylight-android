package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.FragmentActivity
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class RuntimePermissionRequester(
    private val onNewPermissions: (List<Permission>) -> Unit,
) : PermissionRequester {

    override suspend fun request(activity: FragmentActivity, vararg requested: Permission.Type) {
        if (requested.isEmpty()) return
        val requestedDistinct = requested.distinctBy { it.key }
        logInfo { "Requesting permission for $requestedDistinct" }
        val keys = requestedDistinct.flatMap { type ->
            when (type) {
                is Permission.Type.Location -> listOfNotNull(type.key, type.backgroundKey)
            }
        }

        // The implementation of this lib is a bit wonky. askPermission returns a result,
        // but throws whenever the result is not granted. Hence the catch
        val newPermissions = try {
            activity.askPermission(*keys.toTypedArray())
            requestedDistinct.allGranted()
        } catch (e: PermissionException) {
            requestedDistinct.mapNotNull { type ->
                when (type) {
                    is Permission.Type.Location -> {
                        when {
                            type.backgroundKey in e.accepted -> Permission.Location.Granted.WithBackground
                            type.key in e.accepted && type.backgroundKey == null -> {
                                Permission.Location.Granted.WithBackground
                            }
                            type.key in e.accepted -> Permission.Location.Granted.WithoutBackground
                            type.key in e.foreverDenied -> Permission.Location.DeniedForever
                            type.key in e.denied -> Permission.Location.Denied
                            else -> {
                                logError { "Unexpected state for key: ${type.key}: $e" }
                                return@mapNotNull null
                            }
                        }
                    }
                }
            }
        }
        logInfo { "Permissions are $newPermissions" }
        onNewPermissions(newPermissions)
    }

    private fun Collection<Permission.Type>.allGranted(): List<Permission.Location.Granted> {
        return map { type ->
            when (type) {
                Permission.Type.Location -> Permission.Location.Granted.WithBackground
            }
        }
    }
}
