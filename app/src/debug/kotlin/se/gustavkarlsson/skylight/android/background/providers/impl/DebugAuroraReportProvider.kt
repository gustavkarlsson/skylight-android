package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Address
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.models.*
import se.gustavkarlsson.skylight.android.settings.DebugSettings
import java.util.*

class DebugAuroraReportProvider(
		private val debugSettings: DebugSettings,
		private val clock: Clock
) : AuroraReportProvider {

    override fun getReport(timeout: Duration): AuroraReport {
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
