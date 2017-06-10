package se.gustavkarlsson.skylight.android.background.providers

import se.gustavkarlsson.skylight.android.models.factors.Visibility

interface VisibilityProvider {
    fun getVisibility(latitude: Double, longitude: Double): Visibility
}
