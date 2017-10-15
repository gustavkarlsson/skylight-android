package se.gustavkarlsson.skylight.android.test

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.components.TestApplicationComponent
import kotlin.reflect.KClass

class ApplicationComponentActivityTestRule<T : Activity>(
    activityClass: KClass<T>,
	initialTouchMode: Boolean = false,
	launchActivity: Boolean = false,
    private val createComponent: () -> TestApplicationComponent
) : ActivityTestRule<T>(activityClass.java, initialTouchMode, launchActivity) {

	lateinit var component: TestApplicationComponent

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
		component = createComponent()
		Skylight.instance.component = component
    }

	fun launchActivity() {
		launchActivity(null)
	}
}
