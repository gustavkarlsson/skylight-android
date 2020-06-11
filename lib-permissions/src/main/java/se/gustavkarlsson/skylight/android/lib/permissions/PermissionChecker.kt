package se.gustavkarlsson.skylight.android.lib.permissions

import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Access

interface PermissionChecker {
    val access: Observable<Access>
    fun refresh()
}
