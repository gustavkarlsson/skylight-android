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
}
