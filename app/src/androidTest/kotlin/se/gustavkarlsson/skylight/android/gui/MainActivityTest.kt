package se.gustavkarlsson.skylight.android.gui

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
import se.gustavkarlsson.skylight.android.testAppComponent

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

	private val testLocationProvider: TestLocationProvider by lazy {
		testAppComponent.testLocationProvider
	}

    @Rule
	@JvmField
    var testRule = ApplicationComponentActivityTestRule(MainActivity::class)

	@Before
	fun setUp() {
		clearCache()
		clearSharedPreferences()
		testRule.launchActivity()
	}

	@Test
    fun auroraFactorsSectionShown() {
        onView(withId(R.id.auroraFactorsSection)).check(matches(isDisplayed()))
    }

    @Test
    fun auroraChanceSectionShown() {
        onView(withId(R.id.auroraChanceSection)).check(matches(isDisplayed()))
    }

	@Test
	fun unknownErrorWhenRefreshingShowsErrorToast() {
		testLocationProvider.delegate = { throw RuntimeException("ERROR!") }
		onView(withId(R.id.swipeRefreshLayout)).perform(ViewActions.swipeDown())
		onView(withText(R.string.error_unknown_update_error)).inRoot(withDecorView(not(testRule.activity.window.decorView))).check(matches(isDisplayed()))
	}
}
