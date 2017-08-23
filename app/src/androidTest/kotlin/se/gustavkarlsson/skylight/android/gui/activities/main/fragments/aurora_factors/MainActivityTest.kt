package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Rule
	@JvmField
    var testRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun auroraFactorsFragmentShown() {
        onView(withId(R.id.auroraFactorsFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun auroraChanceFragmentShown() {
        onView(withId(R.id.auroraChanceFragment)).check(matches(isDisplayed()))
    }
}