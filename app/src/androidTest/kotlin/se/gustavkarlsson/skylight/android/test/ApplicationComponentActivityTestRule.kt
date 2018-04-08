package se.gustavkarlsson.skylight.android.test

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import kotlin.reflect.KClass

class ApplicationComponentActivityTestRule<T : Activity>(
    activityClass: KClass<T>,
	initialTouchMode: Boolean = false,
	launchActivity: Boolean = false
) : ActivityTestRule<T>(activityClass.java, initialTouchMode, launchActivity) {

	fun launchActivity() {
		launchActivity(null)
	}
}
