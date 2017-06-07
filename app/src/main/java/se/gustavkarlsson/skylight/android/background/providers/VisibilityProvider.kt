package se.gustavkarlsson.skylight.android.background.providers

import se.gustavkarlsson.skylight.android.models.factors.Visibility

internal interface VisibilityProvider {
    fun getVisibility(latitude: Double, longitude: Double): Visibility
}
