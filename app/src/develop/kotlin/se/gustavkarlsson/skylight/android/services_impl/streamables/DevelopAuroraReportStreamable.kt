package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

class DevelopAuroraReportStreamable(
	private val realStreamable: Streamable<AuroraReport>,
	private val developSettings: DevelopSettings,
	private val timeProvider: TimeProvider,
	private val pollingInterval: Duration
) : Streamable<AuroraReport> {

	override val stream: Flowable<AuroraReport>
		get() = developSettings.overrideValuesChanges
			.switchMap { enabled ->
				if (enabled) {
					Single.fromCallable {
						val auroraFactors = createDebugFactors()
						val timestamp = timeProvider.getTime().blockingGet()
						AuroraReport(timestamp, "Fake Location", auroraFactors)
					}.repeatWhen { it.delay(pollingInterval) }
				} else {
					realStreamable.stream
				}
			}

	private fun createDebugFactors(): AuroraFactors {
		val kpIndex = KpIndex(developSettings.kpIndex)
		val geomagLocation = GeomagLocation(developSettings.geomagLatitude)
		val darkness = Darkness(developSettings.sunZenithAngle)
		val weather = Weather(developSettings.cloudPercentage)
		return AuroraFactors(kpIndex, geomagLocation, darkness, weather)
	}
}
