package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuroraChanceFragmentTest {

	@Rule
	public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void locationTextShown() throws Exception {
		onView(withId(R.id.location)).check(matches(isDisplayed()));
	}

	@Test
	public void chanceTextShown() throws Exception {
		onView(withId(R.id.chance)).check(matches(isDisplayed()));
	}

	@Test
	public void timeSinceUpdateTextShown() throws Exception {
		onView(withId(R.id.time_since_update)).check(matches(isDisplayed()));
	}
}
