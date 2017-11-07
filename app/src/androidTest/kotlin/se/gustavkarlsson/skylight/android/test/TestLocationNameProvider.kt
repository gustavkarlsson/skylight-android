package se.gustavkarlsson.skylight.android.test

import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

class TestLocationNameProvider(
	var delegate: () -> String?
) : LocationNameProvider {
	override fun getLocationName(location: Location): String? {
		return delegate()
	}
}
