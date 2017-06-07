package se.gustavkarlsson.skylight.android.background.providers

import android.location.Location

import org.threeten.bp.Duration

import se.gustavkarlsson.skylight.android.models.AuroraFactors

internal interface AuroraFactorsProvider {
    fun getAuroraFactors(location: Location, timeout: Duration): AuroraFactors
}
