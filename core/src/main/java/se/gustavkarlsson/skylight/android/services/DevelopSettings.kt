package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import org.threeten.bp.Duration

interface DevelopSettings {
	val overrideValues: Boolean
	val overrideValuesChanges: Flowable<Boolean>

	val kpIndex: Double

	val geomagLatitude: Double

	val sunZenithAngle: Double

	val cloudPercentage: Int

	val refreshDuration: Duration
}
