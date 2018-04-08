package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.test.*
import se.gustavkarlsson.skylight.android.testappComponent

@RunWith(AndroidJUnit4::class)
@LargeTest
class AuroraChanceFragmentTest {

	private val testLocationNameProvider: TestLocationNameProvider by lazy {
		testappComponent.testLocationNameProvider
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
	fun locationTextShowsActualLocation() {
		onView(withId(R.id.swipeRefreshLayout)).perform(ViewActions.swipeDown())
		waitForView(2000L, allOf(isDescendantOfA(withId(R.id.action_bar)), withText(testLocationNameProvider.delegate().get())), isDisplayed())
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
