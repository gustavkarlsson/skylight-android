package se.gustavkarlsson.skylight.android.test

import android.app.Activity
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.rule.ActivityTestRule
import kotlin.reflect.KClass

class ManualLaunchActivityTestRule<T : Activity>(
    activityClass: KClass<T>,
	initialTouchMode: Boolean = false
) : IntentsTestRule<T>(activityClass.java, initialTouchMode, false) {

	fun launchActivity() {
		launchActivity(null)
	}
}
