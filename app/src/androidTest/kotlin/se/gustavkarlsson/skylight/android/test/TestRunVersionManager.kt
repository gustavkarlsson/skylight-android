package se.gustavkarlsson.skylight.android.test

import se.gustavkarlsson.skylight.android.services.RunVersionManager

class TestRunVersionManager(
	var delegateIsFirstRun: () -> Boolean
) : RunVersionManager {

	override val isFirstRun: Boolean
		get() = delegateIsFirstRun()

	override fun signalFirstRunCompleted() = Unit

}
