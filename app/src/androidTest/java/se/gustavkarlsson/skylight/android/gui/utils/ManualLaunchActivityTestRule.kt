package se.gustavkarlsson.skylight.android.gui.utils

import android.app.Activity
import androidx.test.espresso.intent.rule.IntentsTestRule
import kotlin.reflect.KClass

class ManualLaunchActivityTestRule<T : Activity>(
    activityClass: KClass<T>,
	initialTouchMode: Boolean = false
) : IntentsTestRule<T>(activityClass.java, initialTouchMode, false) {

	fun launchActivity() {
		launchActivity(null)
	}
}
