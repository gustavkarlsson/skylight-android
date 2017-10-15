package se.gustavkarlsson.skylight.android.test

import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

class TestLocationNameProvider(
	var delegate: () -> String?
) : LocationNameProvider {
	suspend override fun getLocationName(latitude: Double, longitude: Double): String? {
		return delegate()
	}
}
