package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.entities.AuroraFactors
import se.gustavkarlsson.skylight.android.services.Location

interface AuroraFactorsProvider {
    fun getAuroraFactors(location: Location): AuroraFactors
}
