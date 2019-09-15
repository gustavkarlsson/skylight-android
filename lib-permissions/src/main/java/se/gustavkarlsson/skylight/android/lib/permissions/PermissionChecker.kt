package se.gustavkarlsson.skylight.android.lib.permissions

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Permission

interface PermissionChecker {
	val permission: Flowable<Permission>
	fun refresh()
}
