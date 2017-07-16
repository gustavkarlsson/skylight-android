package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Address
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.settings.DebugSettings
import java.util.*

class DebugAuroraReportProvider(
		private val debugSettings: DebugSettings,
		private val clock: Clock
) : AuroraReportProvider {

    override fun get(): AuroraReport {
        val location = Address(Locale.ENGLISH)
        val auroraFactors = createAuroraFactors()
        return AuroraReport(clock.now, location, auroraFactors)
    }

    private fun createAuroraFactors(): AuroraFactors {
        val geomagActivity = GeomagActivity(debugSettings.kpIndex)
        val geomagLocation = GeomagLocation(debugSettings.geomagLatitude)
        val darkness = Darkness(debugSettings.sunZenithAngle)
        val visibility = Visibility(debugSettings.cloudPercentage)
        return AuroraFactors(geomagActivity, geomagLocation, darkness, visibility)
    }
}
