package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.services.Location

interface LocationNameProvider {
    fun getLocationName(location: Location): String?
}
