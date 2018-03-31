package se.gustavkarlsson.skylight.android.gui.activities.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.components.DaggerTestApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule
import se.gustavkarlsson.skylight.android.dagger.modules.TestLocationModule
import se.gustavkarlsson.skylight.android.dagger.modules.TestLocationNameModule
import se.gustavkarlsson.skylight.android.dagger.modules.TestSharedPreferencesModule
import se.gustavkarlsson.skylight.android.test.*
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

	@Inject lateinit var testLocationProvider: TestLocationProvider

    @Rule
	@JvmField
    var testRule = ApplicationComponentActivityTestRule(MainActivity::class, false, false) {
		DaggerTestApplicationComponent.builder()
			.contextModule(ContextModule(Skylight.instance))
			.testSharedPreferencesModule(TestSharedPreferencesModule())
			.testLocationModule(TestLocationModule())
			.testLocationNameModule(TestLocationNameModule())
			.build()
	}

	@Before
	fun setUp() {
		initMocks()
		clearCache()
		clearSharedPreferences()
		testRule.launchActivity()
		testRule.component.inject(this)
	}

	@Test
    fun auroraFactorsFragmentShown() {
        onView(withId(R.id.auroraFactorsFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun auroraChanceFragmentShown() {
        onView(withId(R.id.auroraChanceFragment)).check(matches(isDisplayed()))
    }

	@Test
	fun errorWhenRefreshingShowsErrorToast() {
		testLocationProvider.delegate = { throw RuntimeException("ERROR!") }
		onView(withId(R.id.swipeRefreshLayout)).perform(ViewActions.swipeDown())
		onView(withText(R.string.error_unknown_update_error)).inRoot(withDecorView(not(testRule.activity.window.decorView))).check(matches(isDisplayed()))
	}
}
