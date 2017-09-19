package se.gustavkarlsson.skylight.android.services.providers

import android.location.Location

import org.threeten.bp.Duration

interface LocationProvider {
    suspend fun getLocation(): Location
}
