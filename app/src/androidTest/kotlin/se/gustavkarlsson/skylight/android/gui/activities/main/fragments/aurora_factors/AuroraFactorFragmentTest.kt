package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import se.gustavkarlsson.skylight.android.DaggerTest
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class AuroraFactorFragmentTest : DaggerTest() {

    @Rule
	@JvmField
    var testRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun geomagActivityFactorViewShown() {
        onView(withId(R.id.geomagActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun geomagLocationFactorViewShown() {
        onView(withId(R.id.geomagLocation)).check(matches(isDisplayed()))
    }

    @Test
    fun visibilityFactorViewShown() {
        onView(withId(R.id.visibility)).check(matches(isDisplayed()))
    }

    @Test
    fun darknessFactorViewShown() {
        onView(withId(R.id.darkness)).check(matches(isDisplayed()))
    }

    @Test
    fun clickGeomagActivity_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.geomagActivity, R.string.factor_geomag_activity_title_full, R.string.factor_geomag_activity_desc)
    }

    @Test
    fun clickGeomagLocation_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.geomagLocation, R.string.factor_geomag_location_title_full, R.string.factor_geomag_location_desc)
    }

    @Test
    fun clickVisibility_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.visibility, R.string.factor_visibility_title_full, R.string.factor_visibility_desc)
    }

    @Test
    fun clickDarkness_detailViewShown() {
        whenFactorViewClickedDetailViewOpens(R.id.darkness, R.string.factor_darkness_title_full, R.string.factor_darkness_desc)
    }

    private fun whenFactorViewClickedDetailViewOpens(viewId: Int, titleString: Int, descriptionString: Int) {
        onView(withId(viewId)).perform(click())
        onView(withText(titleString)).check(matches(isDisplayed()))
        onView(withText(descriptionString)).check(matches(isDisplayed()))
    }
}
