package se.gustavkarlsson.skylight.android.test

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import kotlin.reflect.KClass

class ApplicationComponentActivityTestRule<T : Activity>(
    activityClass: KClass<T>,
	initialTouchMode: Boolean = false,
	launchActivity: Boolean = false,
    private val createComponent: () -> ApplicationComponent
) : ActivityTestRule<T>(activityClass.java, initialTouchMode, launchActivity) {
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        Skylight.instance.component = createComponent()
    }

	fun launchActivity() {
		launchActivity(null)
	}
}
