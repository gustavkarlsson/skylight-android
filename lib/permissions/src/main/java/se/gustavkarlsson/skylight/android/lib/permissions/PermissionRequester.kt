package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.FragmentActivity

interface PermissionRequester {
    suspend fun request(activity: FragmentActivity, permission: Permission)
}
