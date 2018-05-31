package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

class DevelopAuroraReportProvider(
	private val realProvider: AuroraReportProvider,
	private val developSettings: DevelopSettings,
	private val timeProvider: TimeProvider
) : AuroraReportProvider {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
			if (developSettings.overrideValues) {
				val auroraFactors = createDebugFactors()
				val locationName = "Fake Location"
				val timestamp = timeProvider.getTime().blockingGet()
				Single.just(AuroraReport(timestamp, locationName, auroraFactors))
					.delay(developSettings.refreshDuration)
			} else {
				realProvider.get()
			}
		}.flatMap { it }

	}

	private fun createDebugFactors(): AuroraFactors {
		val kpIndex = KpIndex(developSettings.kpIndex)
		val geomagLocation = GeomagLocation(developSettings.geomagLatitude)
		val darkness = Darkness(developSettings.sunZenithAngle)
		val weather = Weather(developSettings.cloudPercentage)
		return AuroraFactors(kpIndex, geomagLocation, darkness, weather)
	}
}
