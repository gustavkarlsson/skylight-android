package se.gustavkarlsson.skylight.android.gui.screens.intro

import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import assertk.assert
import assertk.assertions.isTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import se.gustavkarlsson.skylight.android.gui.MainActivity
import se.gustavkarlsson.skylight.android.gui.screens.main.MainScreen
import se.gustavkarlsson.skylight.android.gui.screens.permission.PermissionScreen
import se.gustavkarlsson.skylight.android.test.ManualLaunchActivityTestRule
import se.gustavkarlsson.skylight.android.test.TestPermissionChecker
import se.gustavkarlsson.skylight.android.test.TestRunVersionManager
import se.gustavkarlsson.skylight.android.test.verifyPrivacyPolicyOpened

@RunWith(AndroidJUnit4::class)
@LargeTest
class IntroScreenTest : KoinComponent {

	@Rule
	@JvmField
	var testRule = ManualLaunchActivityTestRule(MainActivity::class)

	private val testRunVersionManager: TestRunVersionManager by inject()
	private val testTestPermissionChecker: TestPermissionChecker by inject()

	private val screen = IntroScreen()
	private val permissionScreen = PermissionScreen()
	private val mainScreen = MainScreen()

	@Before
	fun setUp() {
		testRunVersionManager.isFirstRunRelay.accept(true)
		testRule.launchActivity()
	}

	@Test
	fun clickingPrivacyPolicyOpensIt() {
		screen.privacyPolicyLink.click()
		verifyPrivacyPolicyOpened()
	}

	@Test
	fun clickingNextWithNoLocationPermissionOpensPermissionScreen() {
		testTestPermissionChecker.isLocationGrantedRelay.accept(false)
		screen.nextButton.click()
		permissionScreen.isDisplayed()
	}

	@Test
	fun clickingNextOpensMainScreen() {
		screen.nextButton.click()
		mainScreen.isDisplayed()
	}

	@Test
	fun clickingBackFinishesActivity() {
		pressBackUnconditionally()
		assert(testRule.activity.isFinishing, "activity is finishing").isTrue()
	}
}
