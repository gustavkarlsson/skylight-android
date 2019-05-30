package se.gustavkarlsson.skylight.android.feature.main

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Permission

internal interface PermissionChecker {
	val permission: Flowable<Permission>
	fun signalDeniedForever()
	fun refresh()
}
