package se.gustavkarlsson.skylight.android.lib.permissions

import androidx.fragment.app.Fragment

interface PermissionRequester {
    suspend fun request(fragment: Fragment)
}
