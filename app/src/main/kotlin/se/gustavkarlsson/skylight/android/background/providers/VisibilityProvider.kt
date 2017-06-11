package se.gustavkarlsson.skylight.android.background.providers

import se.gustavkarlsson.skylight.android.models.Visibility

interface VisibilityProvider {
    fun getVisibility(latitude: Double, longitude: Double): Visibility
}
