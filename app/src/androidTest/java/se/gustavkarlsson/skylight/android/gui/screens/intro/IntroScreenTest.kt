package se.gustavkarlsson.skylight.android.gui.screens.intro

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
import se.gustavkarlsson.skylight.android.gui.screens.main.MainScreen
import se.gustavkarlsson.skylight.android.gui.screens.permission.PermissionScreen
import se.gustavkarlsson.skylight.android.gui.utils.ManualLaunchActivityTestRule
import se.gustavkarlsson.skylight.android.gui.utils.verifyIsFinishing
import se.gustavkarlsson.skylight.android.gui.utils.verifyPrivacyPolicyOpened
import se.gustavkarlsson.skylight.android.modules.TestPermissionChecker
import se.gustavkarlsson.skylight.android.modules.TestRunVersionManager

@RunWith(AndroidJUnit4::class)
@LargeTest
class IntroScreenTest : KoinComponent {

	@Rule
	@JvmField
	var testRule = ManualLaunchActivityTestRule(MainActivity::class)

	private val testRunVersionManager: TestRunVersionManager by inject()
	private val testTestPermissionChecker: TestPermissionChecker by inject()

	@Before
	fun setUp() {
		testRunVersionManager.isFirstRunRelay.accept(true)
		testRule.launchActivity()
	}

	@Test
	fun clickingPrivacyPolicyOpensIt() {
		IntroScreen.privacyPolicyLink.click()
		verifyPrivacyPolicyOpened()
	}

	@Test
	fun clickingNextWithNoLocationPermissionOpensPermissionScreen() {
		testTestPermissionChecker.isLocationGrantedRelay.accept(false)
		IntroScreen.nextButton.click()
		PermissionScreen.isDisplayed()
	}

	@Test
	fun clickingNextOpensMainScreen() {
		IntroScreen.nextButton.click()
		MainScreen.isDisplayed()
	}

	@Test
	fun clickingBackFinishesActivity() {
		pressBackUnconditionally()
		testRule.activity.verifyIsFinishing()
	}
}
