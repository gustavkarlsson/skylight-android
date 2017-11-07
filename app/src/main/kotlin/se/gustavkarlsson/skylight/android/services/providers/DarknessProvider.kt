package se.gustavkarlsson.skylight.android.services.providers

import org.threeten.bp.Instant

import se.gustavkarlsson.skylight.android.entities.Darkness

interface DarknessProvider {
    fun getDarkness(time: Instant, latitude: Double, longitude: Double): Darkness
}
