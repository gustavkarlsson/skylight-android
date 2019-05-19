package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable

interface GooglePlayServicesChecker {
	val isAvailable: Flowable<Boolean>
	fun signalInstalled()
}
