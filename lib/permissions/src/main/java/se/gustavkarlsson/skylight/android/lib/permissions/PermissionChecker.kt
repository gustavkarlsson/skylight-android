package se.gustavkarlsson.skylight.android.lib.permissions

import kotlinx.coroutines.flow.StateFlow

interface PermissionChecker {
    val permissions: StateFlow<Permissions>
    fun refresh()
}
