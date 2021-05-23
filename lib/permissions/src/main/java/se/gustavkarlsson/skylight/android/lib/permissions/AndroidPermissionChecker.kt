package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class AndroidPermissionChecker(
    private val context: Context,
    private val state: MutableStateFlow<Permissions>,
) : PermissionChecker {

    override val permissions: StateFlow<Permissions> = state

    override fun refresh() {
        val old = state.value
        val newPermissionsList = getNewPermissions(old)
        val new = newPermissionsList.fold(old) { acc, permission ->
            acc.update(permission)
        }
        if (new != old) {
            state.value = new
            logInfo { "Permissions changed from $old to $new" }
        }
    }

    private fun getNewPermissions(old: Permissions): List<Permission> {
        return old.properties.map { property ->
            when (property) {
                is LocationProperty -> getLocationPermission()
            }
        }
    }

    private fun getLocationPermission(): Permission.Location {
        val locationResult = ContextCompat.checkSelfPermission(context, Permission.Type.Location.key)
        return if (locationResult == PackageManager.PERMISSION_GRANTED) {
            getBackgroundLocationPermission()
        } else Permission.Location.Denied
    }

    private fun getBackgroundLocationPermission(): Permission.Location.Granted {
        val key = Permission.Type.Location.backgroundKey ?: return Permission.Location.Granted.WithBackground
        val backgroundResult = ContextCompat.checkSelfPermission(context, key)
        return if (backgroundResult == PackageManager.PERMISSION_GRANTED) {
            Permission.Location.Granted.WithBackground
        } else Permission.Location.Granted.WithoutBackground
    }
}
