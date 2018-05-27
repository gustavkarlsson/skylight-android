package se.gustavkarlsson.skylight.android.gui.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
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
import se.gustavkarlsson.skylight.android.test.ApplicationComponentActivityTestRule
import se.gustavkarlsson.skylight.android.test.clearCache
import se.gustavkarlsson.skylight.android.test.clearSharedPreferences

@RunWith(AndroidJUnit4::class)
@LargeTest
class FactorsTest {

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
    fun kpIndexFactorViewShown() {
        onView(withId(R.id.kpIndexCard)).check(matches(isDisplayed()))
    }

    @Test
    fun geomagLocationFactorViewShown() {
        onView(withId(R.id.geomagLocationCard)).check(matches(isDisplayed()))
    }

    @Test
    fun weatherFactorViewShown() {
        onView(withId(R.id.weatherCard)).check(matches(isDisplayed()))
    }

    @Test
    fun darknessFactorViewShown() {
        onView(withId(R.id.darknessCard)).check(matches(isDisplayed()))
    }

    @Test
    fun clickKpIndex_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.kpIndexCard, R.string.factor_kp_index_title_full, R.string.factor_kp_index_desc)
    }

    @Test
    fun clickGeomagLocation_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.geomagLocationCard, R.string.factor_geomag_location_title_full, R.string.factor_geomag_location_desc)
    }

    @Test
    fun clickWeather_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.weatherCard, R.string.factor_weather_title_full, R.string.factor_weather_desc)
    }

    @Test
    fun clickDarkness_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.darknessCard, R.string.factor_darkness_title_full, R.string.factor_darkness_desc)
    }

    private fun whenFactorViewClickedDetailViewOpens(viewId: Int, titleString: Int, descriptionString: Int) {
        onView(withId(viewId)).perform(click())
        onView(withText(titleString)).check(matches(isDisplayed()))
        onView(withText(descriptionString)).check(matches(isDisplayed()))
		pressBack()
    }
}
