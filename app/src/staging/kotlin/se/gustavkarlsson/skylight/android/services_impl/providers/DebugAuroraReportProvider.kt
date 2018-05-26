package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

class DebugAuroraReportProvider(
	private val realProvider: AuroraReportProvider,
	private val debugSettings: DebugSettings,
	private val timeProvider: TimeProvider
) : AuroraReportProvider {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
			if (debugSettings.overrideValues) {
				val auroraFactors = createDebugFactors()
				val locationName = "Fake Location"
				val timestamp = timeProvider.getTime().blockingGet()
				Single.just(AuroraReport(timestamp, locationName, auroraFactors))
					.delay(debugSettings.refreshDuration)
			} else {
				realProvider.get()
			}
		}.flatMap { it }

	}

	private fun createDebugFactors(): AuroraFactors {
		val kpIndex = KpIndex(debugSettings.kpIndex)
		val geomagLocation = GeomagLocation(debugSettings.geomagLatitude)
		val darkness = Darkness(debugSettings.sunZenithAngle)
		val weather = Weather(debugSettings.cloudPercentage)
		return AuroraFactors(kpIndex, geomagLocation, darkness, weather)
	}
}
