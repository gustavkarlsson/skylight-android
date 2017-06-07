package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Address
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.models.AuroraFactors
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.models.factors.Darkness
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation
import se.gustavkarlsson.skylight.android.models.factors.Visibility
import se.gustavkarlsson.skylight.android.settings.DebugSettings
import java.util.*

internal class DebugAuroraReportProvider(
		private val debugSettings: DebugSettings,
		private val clock: Clock
) : AuroraReportProvider {

    override fun getReport(timeout: Duration): AuroraReport {
        val location = Address(Locale.ENGLISH)
        val auroraFactors = createAuroraFactors()
        return AuroraReport(clock.millis(), location, auroraFactors)
    }

    private fun createAuroraFactors(): AuroraFactors {
        val geomagActivity = GeomagActivity(debugSettings.kpIndex)
        val geomagLocation = GeomagLocation(debugSettings.geomagLatitude)
        val darkness = Darkness(debugSettings.sunZenithAngle)
        val visibility = Visibility(debugSettings.cloudPercentage)
        return AuroraFactors(geomagActivity, geomagLocation, darkness, visibility)
    }
}
