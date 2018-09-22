package se.gustavkarlsson.skylight.android.test

import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

class TestGooglePlayServicesChecker(
	var delegateIsAvailable: () -> Boolean
) : GooglePlayServicesChecker {

	override val isAvailable: Boolean
		get() = delegateIsAvailable()

}
