package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.components.DaggerTestApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule
import se.gustavkarlsson.skylight.android.dagger.modules.TestSharedPreferencesModule
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.test.ApplicationComponentActivityTestRule
import se.gustavkarlsson.skylight.android.test.initMocks
import se.gustavkarlsson.skylight.android.test.waitForView

@RunWith(AndroidJUnit4::class)
@LargeTest
class AuroraChanceFragmentTest {

	@Mock
	lateinit var mockSettings: Settings

	@Mock
	lateinit var mockLocationProvider: LocationProvider

	@Mock
	lateinit var mockLocationNameProvider: LocationNameProvider

	@Rule
	@JvmField
	var testRule = ApplicationComponentActivityTestRule(MainActivity::class, false, false) {
		DaggerTestApplicationComponent.builder()
			.contextModule(ContextModule(Skylight.instance))
			.testSharedPreferencesModule(TestSharedPreferencesModule())
			.build()
	}

	@Before
	fun setUp() {
		initMocks()
		runBlocking { // TODO Can this be done outside of coroutine?
			whenever(mockLocationProvider.getLocation()).thenReturn(Location(0.0, 0.0))
			whenever(mockLocationNameProvider.getLocationName(any(), any())).thenReturn("Garden of Eden")
		}
		testRule.launchActivity()
	}

    @Test
    fun locationTextShown() {
		waitForView(2000L, withId(R.id.location), isDisplayed())
        onView(withId(R.id.location)).check(matches(isDisplayed()))
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
