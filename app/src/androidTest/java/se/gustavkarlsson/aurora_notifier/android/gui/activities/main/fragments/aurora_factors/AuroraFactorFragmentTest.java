package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

// TODO these tests take forever...
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuroraFactorFragmentTest {

	@Rule
	public ActivityTestRule<MainActivity> testRule =
			new ActivityTestRule<>(MainActivity.class);

	@Test
	public void fragmentShown() throws Exception {
		onView(withId(R.id.fragment_aurora_chance)).check(matches(isDisplayed()));
	}

	@Test
	public void factorViewsShown() throws Exception {
		onView(withId(R.id.aurora_factor_geomag_activity)).check(matches(isDisplayed()));
		onView(withId(R.id.aurora_factor_geomag_location)).check(matches(isDisplayed()));
		onView(withId(R.id.aurora_factor_visibility)).check(matches(isDisplayed()));
		onView(withId(R.id.aurora_factor_darkness)).check(matches(isDisplayed()));
	}

	@Test
	public void clickGeomagActivity_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_geomag_activity, R.string.factor_geomag_activity_title, R.string.factor_geomag_activity_desc);
	}

	@Test
	public void clickGeomagLocation_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_geomag_location, R.string.factor_geomag_location_title, R.string.factor_geomag_location_desc);
	}

	@Test
	public void clickVisibility_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_visibility, R.string.factor_visibility_title, R.string.factor_visibility_desc);
	}

	@Test
	public void clickDarkness_detailViewShown() throws Exception {
		whenFactorViewClickedDetailViewOpens(R.id.aurora_factor_darkness, R.string.factor_darkness_title, R.string.factor_darkness_desc);
	}

	private static void whenFactorViewClickedDetailViewOpens(int viewId, int titleString, int descriptionString) {
		onView(withId(viewId)).perform(click());
		onView(withText(titleString)).check(matches(isDisplayed()));
		onView(withText(descriptionString)).check(matches(isDisplayed()));
	}
}
