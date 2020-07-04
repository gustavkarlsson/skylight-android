package se.gustavkarlsson.skylight.android.lib.permissions

import io.reactivex.Observable

interface PermissionChecker {
    val access: Observable<Access>
    fun refresh()
}
