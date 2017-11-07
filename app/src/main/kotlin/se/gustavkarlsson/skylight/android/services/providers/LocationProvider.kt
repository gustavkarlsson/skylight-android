package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.services.Location


interface LocationProvider {
    fun getLocation(): Location
}
