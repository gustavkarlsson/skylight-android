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
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.components.DaggerTestApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule
import se.gustavkarlsson.skylight.android.dagger.modules.TestLocationNameProviderModule
import se.gustavkarlsson.skylight.android.dagger.modules.TestLocationProviderModule
import se.gustavkarlsson.skylight.android.dagger.modules.TestSharedPreferencesModule
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.test.*
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class AuroraChanceFragmentTest {

	@Inject lateinit var testLocationProvider: TestLocationProvider

	@Inject lateinit var testLocationNameProvider: TestLocationNameProvider

	@Rule
	@JvmField
	var testRule = ApplicationComponentActivityTestRule(MainActivity::class, false, false) {
		DaggerTestApplicationComponent.builder()
			.contextModule(ContextModule(Skylight.instance))
			.testSharedPreferencesModule(TestSharedPreferencesModule())
			.testLocationProviderModule(TestLocationProviderModule())
			.testLocationNameProviderModule(TestLocationNameProviderModule())
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
    fun locationTextShownImmediately() {
		onView(allOf(isDescendantOfA(withId(R.id.action_bar)), withText(R.string.your_location))).check(matches(isDisplayed()))
    }

	@Test
	fun locationTextUpdatesToActualLocation() {
		onView(withId(R.id.swipeRefreshLayout)).perform(ViewActions.swipeDown())
		waitForView(2000L, allOf(isDescendantOfA(withId(R.id.action_bar)), withText(testLocationNameProvider.delegate())), isDisplayed())
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
