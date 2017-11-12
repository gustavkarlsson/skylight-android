package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Single
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import java.util.concurrent.TimeUnit

class DebugAuroraReportProvider(
	private val realProvider: AuroraReportProvider,
	private val debugSettings: DebugSettings,
	private val clock: Clock
) : AuroraReportProvider {

	override fun get(): Single<AuroraReport> {
		if (debugSettings.overrideValues) {
			return Single.fromCallable {
				val auroraFactors = createAuroraFactors()
				AuroraReport(clock.now, "Fake Location", auroraFactors)
			}.delay(5, TimeUnit.SECONDS)

		}
		return realProvider.get()
	}

	private fun createAuroraFactors(): AuroraFactors {
		val kpIndex = KpIndex(debugSettings.kpIndex)
		val geomagLocation = GeomagLocation(debugSettings.geomagLatitude)
		val darkness = Darkness(debugSettings.sunZenithAngle)
		val visibility = Visibility(debugSettings.cloudPercentage)
		return AuroraFactors(kpIndex, geomagLocation, darkness, visibility)
	}
}
