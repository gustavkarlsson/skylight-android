package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable

interface PermissionChecker {
	val isLocationGranted: Flowable<Boolean>
	fun signalPermissionGranted()
}
