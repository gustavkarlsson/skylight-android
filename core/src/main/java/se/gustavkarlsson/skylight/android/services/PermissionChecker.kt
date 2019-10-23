package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Permission

interface PermissionChecker {
	val permission: Flowable<Permission>
	fun refresh()
}
