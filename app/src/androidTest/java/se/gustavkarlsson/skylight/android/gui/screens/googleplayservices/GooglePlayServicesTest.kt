package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import se.gustavkarlsson.skylight.android.MainActivity
import se.gustavkarlsson.skylight.android.gui.utils.ManualLaunchActivityTestRule
import se.gustavkarlsson.skylight.android.gui.utils.verifyIsFinishing
import se.gustavkarlsson.skylight.android.modules.TestGooglePlayServicesChecker

@RunWith(AndroidJUnit4::class)
@LargeTest
class GooglePlayServicesTest : KoinComponent {

	@Rule
	@JvmField
	var testRule = ManualLaunchActivityTestRule(MainActivity::class)

	private val testGooglePlayServicesChecker: TestGooglePlayServicesChecker by inject()

	@Before
	fun setUp() {
		testGooglePlayServicesChecker.isAvailableRelay.accept(false)
		testRule.launchActivity()
	}

	@Test
	fun clickingBackFinishesActivity() {
		pressBackUnconditionally()
		testRule.activity.verifyIsFinishing()
	}

	// TODO Add tests simulating installing Google Play Services
}
