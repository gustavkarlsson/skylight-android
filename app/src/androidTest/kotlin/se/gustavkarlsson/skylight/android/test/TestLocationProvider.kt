package se.gustavkarlsson.skylight.android.test

import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

class TestLocationProvider(
	var delegate: () -> Location
) : LocationProvider {

    override fun getLocation(): Location {
        return delegate()
    }
}
