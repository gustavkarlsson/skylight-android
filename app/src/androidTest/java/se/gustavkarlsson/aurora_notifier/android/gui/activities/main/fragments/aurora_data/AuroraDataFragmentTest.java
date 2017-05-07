package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_data;

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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuroraDataFragmentTest {

	@Rule
	public ActivityTestRule<MainActivity> testRule =
			new ActivityTestRule<>(MainActivity.class);

	@Test
	public void fragmentShown() throws Exception {
		onView(withId(R.id.fragment_aurora_chance)).check(matches(isDisplayed()));
	}

	@Test
	public void dataViewsShown() throws Exception {
		onView(withId(R.id.aurora_data_solar_activity)).check(matches(isDisplayed()));
		onView(withId(R.id.aurora_data_geomagnetic_location)).check(matches(isDisplayed()));
		onView(withId(R.id.aurora_data_weather)).check(matches(isDisplayed()));
		onView(withId(R.id.aurora_data_sun_position)).check(matches(isDisplayed()));
	}

	@Test
	public void clickSolarActivity_detailViewShown() throws Exception {
		whenDataViewClickedDetailViewOpens(R.id.aurora_data_solar_activity, R.string.solar_activity_title, R.string.solar_activity_desc);
	}

	@Test
	public void clickGeomagneticLocation_detailViewShown() throws Exception {
		whenDataViewClickedDetailViewOpens(R.id.aurora_data_geomagnetic_location, R.string.geomagnetic_location_title, R.string.geomagnetic_location_desc);
	}

	@Test
	public void clickWeather_detailViewShown() throws Exception {
		whenDataViewClickedDetailViewOpens(R.id.aurora_data_weather, R.string.weather_title, R.string.weather_desc);
	}

	@Test
	public void clickSunPosition_detailViewShown() throws Exception {
		whenDataViewClickedDetailViewOpens(R.id.aurora_data_sun_position, R.string.sun_position_title, R.string.sun_position_desc);
	}

	private static void whenDataViewClickedDetailViewOpens(int viewId, int titleString, int descriptionString) {
		onView(withId(viewId)).perform(click());
		onView(withText(titleString)).check(matches(isDisplayed()));
		onView(withText(descriptionString)).check(matches(isDisplayed()));
	}
}
