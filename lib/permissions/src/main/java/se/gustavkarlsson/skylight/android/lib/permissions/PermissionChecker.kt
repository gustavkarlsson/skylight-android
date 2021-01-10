package se.gustavkarlsson.skylight.android.lib.permissions

import kotlinx.coroutines.flow.Flow

interface PermissionChecker {
    val access: Flow<Access>
    fun refresh()
}
