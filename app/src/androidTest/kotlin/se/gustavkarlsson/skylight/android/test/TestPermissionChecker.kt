package se.gustavkarlsson.skylight.android.test

import se.gustavkarlsson.skylight.android.services.PermissionChecker

class TestPermissionChecker(
	var delegateIsLocationGranted: () -> Boolean
) : PermissionChecker {

	override val isLocationGranted: Boolean
		get() = delegateIsLocationGranted()

}
