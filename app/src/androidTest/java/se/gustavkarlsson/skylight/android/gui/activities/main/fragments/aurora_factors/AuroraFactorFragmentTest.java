package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuroraFactorFragmentTest {

	@Rule
	public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void factorViewsShown() throws Exception {
		onView(ViewMatchers.withId(R.id.aurora_factor_geomag_activity)).check(matches(isDisplayed()));
		onView(ViewMatchers.withId(R.id.aurora_factor_geomag_location)).check(matches(isDisplayed()));
		onView(ViewMatchers.withId(R.id.aurora_factor_visibility)).check(matches(isDisplayed()));
		onView(ViewMatchers.withId(R.id.aurora_factor_darkness)).check(matches(isDisplayed()));
	}

	@Test
	public void clickGeomagActivity_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_geomag_activity, R.string.factor_geomag_activity_title_full, R.string.factor_geomag_activity_desc);
	}

	@Test
	public void clickGeomagLocation_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_geomag_location, R.string.factor_geomag_location_title_full, R.string.factor_geomag_location_desc);
	}

	@Test
	public void clickVisibility_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_visibility, R.string.factor_visibility_title_full, R.string.factor_visibility_desc);
	}

	@Test
	public void clickDarkness_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_darkness, R.string.factor_darkness_title_full, R.string.factor_darkness_desc);
	}

	private static void whenFactorViewClickedDetailViewOpens(int viewId, int titleString, int descriptionString) {
		onView(withId(viewId)).perform(click());
		onView(withText(titleString)).check(matches(isDisplayed()));
		onView(withText(descriptionString)).check(matches(isDisplayed()));
	}
}
