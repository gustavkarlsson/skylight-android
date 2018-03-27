package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable

interface DebugSettings {
	val overrideValues: Boolean
	val overrideValuesChanges: Flowable<Boolean>

	val kpIndex: Double

	val geomagLatitude: Double

	val sunZenithAngle: Double

	val cloudPercentage: Int
}
