package se.gustavkarlsson.skylight.android.background.providers

import org.threeten.bp.Instant

import se.gustavkarlsson.skylight.android.models.factors.Darkness

internal interface DarknessProvider {
    fun getDarkness(time: Instant, latitude: Double, longitude: Double): Darkness
}
