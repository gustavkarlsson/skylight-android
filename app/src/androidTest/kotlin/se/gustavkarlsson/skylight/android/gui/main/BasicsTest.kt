package se.gustavkarlsson.skylight.android.gui.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.MainActivity
import se.gustavkarlsson.skylight.android.test.*
import se.gustavkarlsson.skylight.android.testAppComponent

@RunWith(AndroidJUnit4::class)
@LargeTest
class BasicsTest {

	@Rule
	@JvmField
	var testRule = ApplicationComponentActivityTestRule(MainActivity::class)

	private val testLocationProvider: TestLocationProvider by lazy {
		testAppComponent.testLocationProvider
	}

	private val testLocationNameProvider: TestLocationNameProvider by lazy {
		testAppComponent.testLocationNameProvider
	}

	@Before
	fun setUp() {
		clearCache()
		clearSharedPreferences()
		testRule.launchActivity()
	}

	@Test
	fun unknownErrorWhenRefreshingShowsErrorToast() {
		testLocationProvider.delegate = { throw RuntimeException("ERROR!") }
		onView(withId(R.id.swipeRefreshLayout)).perform(ViewActions.swipeDown())
		onView(withText(R.string.error_unknown_update_error)).check(matches(isDisplayed()))
	}

	@Test
	fun locationTextShowsActualLocation() {
		onView(withId(R.id.swipeRefreshLayout)).perform(ViewActions.swipeDown())
		waitForView(2000L, withText(testLocationNameProvider.delegate().get()), isDisplayed())
	}

	@Test
	fun chanceTextShown() {
		onView(withId(R.id.chance)).check(matches(isDisplayed()))
	}

	@Test
	fun timeSinceUpdateTextShown() {
		waitForView(2000L, withId(R.id.timeSinceUpdate), isDisplayed())
		onView(withId(R.id.timeSinceUpdate)).check(matches(isDisplayed()))
	}
}
