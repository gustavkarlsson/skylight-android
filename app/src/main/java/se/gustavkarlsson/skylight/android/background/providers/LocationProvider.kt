package se.gustavkarlsson.skylight.android.background.providers

import android.location.Location

import org.threeten.bp.Duration

internal interface LocationProvider {
    fun getLocation(timeout: Duration): Location
}
