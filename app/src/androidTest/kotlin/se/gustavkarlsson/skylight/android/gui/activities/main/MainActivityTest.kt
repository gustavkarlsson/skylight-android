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
import se.gustavkarlsson.skylight.android.test.ApplicationComponentActivityTestRule
import se.gustavkarlsson.skylight.android.test.TestLocationProvider
import se.gustavkarlsson.skylight.android.test.clearCache
import se.gustavkarlsson.skylight.android.test.clearSharedPreferences
import se.gustavkarlsson.skylight.android.testappComponent

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

	private val testLocationProvider: TestLocationProvider by lazy {
		testappComponent.testLocationProvider
	}

    @Rule
	@JvmField
    var testRule = ApplicationComponentActivityTestRule(MainActivity::class, false, false)

	@Before
	fun setUp() {
		clearCache()
		clearSharedPreferences()
		testRule.launchActivity()
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
	fun unknownErrorWhenRefreshingShowsErrorToast() {
		testLocationProvider.delegate = { throw RuntimeException("ERROR!") }
		onView(withId(R.id.swipeRefreshLayout)).perform(ViewActions.swipeDown())
		onView(withText(R.string.error_unknown_update_error)).inRoot(withDecorView(not(testRule.activity.window.decorView))).check(matches(isDisplayed()))
	}
}
