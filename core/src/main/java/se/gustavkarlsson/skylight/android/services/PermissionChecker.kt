package se.gustavkarlsson.skylight.android.services

import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Access
import se.gustavkarlsson.skylight.android.entities.Permission

interface PermissionChecker<T : Permission> {
    val access: Observable<Access>
    fun refresh()
}
