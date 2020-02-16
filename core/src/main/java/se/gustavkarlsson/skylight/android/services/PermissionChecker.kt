package se.gustavkarlsson.skylight.android.services

import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.entities.Permission

interface PermissionChecker {
	val permission: Observable<Permission>
	fun refresh()
}
