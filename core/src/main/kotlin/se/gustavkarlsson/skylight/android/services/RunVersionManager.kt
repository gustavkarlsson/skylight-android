package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable

interface RunVersionManager {
	val isFirstRun: Flowable<Boolean>
	fun signalFirstRunCompleted()
}
