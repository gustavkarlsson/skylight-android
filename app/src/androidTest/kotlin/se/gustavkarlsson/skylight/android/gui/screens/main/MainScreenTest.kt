package se.gustavkarlsson.skylight.android.gui.screens.main

import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.filters.LargeTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.agoda.kakao.KView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.MainActivity
import se.gustavkarlsson.skylight.android.gui.utils.ManualLaunchActivityTestRule
import se.gustavkarlsson.skylight.android.gui.utils.verifyIsFinishing
import se.gustavkarlsson.skylight.android.modules.TestLocationNameProvider

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainScreenTest : KoinComponent {

	@Rule
	@JvmField
	var testRule =
		ManualLaunchActivityTestRule(MainActivity::class)

	private val testLocationNameProvider: TestLocationNameProvider by inject()

	@Before
	fun setUp() {
		testRule.launchActivity()
	}

	@Test
	fun locationTextShowsActualLocation() {
		MainScreen.toolbar.hasTitle(testLocationNameProvider.delegate().unsafeValue)
	}

	@Test
	fun everythingIsDisplayed() {
		MainScreen {
			chance.isDisplayed()
			timeSinceUpdate.isDisplayed()
			kpIndexCard.isDisplayed()
			darknessCard.isDisplayed()
			weatherCard.isDisplayed()
			geomagLocationCard.isDisplayed()
		}
	}

	@Test
	fun openAndCloseAllDetailViews() {
		MainScreen {
			verifyFactorClickOpensDetailView(
				kpIndexCard,
				R.string.factor_kp_index_title_full,
				R.string.factor_kp_index_desc
			)
			verifyFactorClickOpensDetailView(
				darknessCard,
				R.string.factor_darkness_title_full,
				R.string.factor_darkness_desc
			)
			verifyFactorClickOpensDetailView(
				geomagLocationCard,
				R.string.factor_geomag_location_title_full,
				R.string.factor_geomag_location_desc
			)
			verifyFactorClickOpensDetailView(
				weatherCard,
				R.string.factor_weather_title_full,
				R.string.factor_weather_desc
			)
		}
	}

	@Test
	fun clickingBackFinishesActivity() {
		pressBackUnconditionally()
		testRule.activity.verifyIsFinishing()
	}

	private fun verifyFactorClickOpensDetailView(cardView: KView, title: Int, description: Int) {
		cardView.click()
		MainScreen {
			detailView.isDisplayed()
			detailView.hasDescendant { withText(title) }
			detailView.hasDescendant { withText(description) }
			outsideDetailView.click()
		}
	}
}
