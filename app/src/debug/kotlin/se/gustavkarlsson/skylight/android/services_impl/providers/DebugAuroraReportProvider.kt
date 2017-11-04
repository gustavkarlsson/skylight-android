package se.gustavkarlsson.skylight.android.services_impl.providers

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.DebugSettings
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import java.util.concurrent.TimeUnit

class DebugAuroraReportProvider(
        private val cache: SingletonCache<AuroraReport>,
        private val realProvider: AuroraReportProvider,
        private val debugSettings: DebugSettings,
        private val clock: Clock
) : AuroraReportProvider {

    override fun get(): AuroraReport {
		if (debugSettings.overrideValues) {
			runBlocking { delay(5, TimeUnit.SECONDS) }
            val auroraFactors = createAuroraFactors()
            val auroraReport = AuroraReport(clock.now, "Umeå", auroraFactors)
			cache.value = auroraReport
            return auroraReport
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
