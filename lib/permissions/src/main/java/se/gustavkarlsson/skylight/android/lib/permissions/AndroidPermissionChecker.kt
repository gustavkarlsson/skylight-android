package se.gustavkarlsson.skylight.android.lib.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalCoroutinesApi
internal class AndroidPermissionChecker(
    private val context: Context,
    private val state: MutableStateFlow<Permissions>,
) : PermissionChecker {

    @FlowPreview
    override val permissions: StateFlow<Permissions> = state

    override fun refresh() {
        val old = state.value
        val fromSystem = old.toMap().keys.map { permission ->
            getAccessFromSystem(permission)
        }
        val new = old.update(fromSystem)
        state.value = new
    }

    private fun getAccessFromSystem(permission: Permission): PermissionAccess {
        val result = ContextCompat.checkSelfPermission(context, permission.key)
        val access = if (result == PackageManager.PERMISSION_GRANTED) {
            Access.Granted
        } else Access.Denied
        return PermissionAccess(permission, access)
    }
}
