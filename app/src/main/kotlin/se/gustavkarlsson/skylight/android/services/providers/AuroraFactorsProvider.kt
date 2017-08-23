package se.gustavkarlsson.skylight.android.services.providers

import android.location.Location

import org.threeten.bp.Duration

import se.gustavkarlsson.skylight.android.entities.AuroraFactors

interface AuroraFactorsProvider {
    fun getAuroraFactors(location: Location, timeout: Duration): AuroraFactors
}