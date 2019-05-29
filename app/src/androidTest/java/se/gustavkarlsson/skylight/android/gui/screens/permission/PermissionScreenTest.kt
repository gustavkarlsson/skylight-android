package se.gustavkarlsson.skylight.android.gui.screens.permission

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
import se.gustavkarlsson.skylight.android.gui.utils.ManualLaunchActivityTestRule
import se.gustavkarlsson.skylight.android.gui.utils.allowPermission
import se.gustavkarlsson.skylight.android.gui.utils.denyPermission
import se.gustavkarlsson.skylight.android.gui.utils.denyPermissionForever
import se.gustavkarlsson.skylight.android.gui.utils.verifyIsFinishing
import se.gustavkarlsson.skylight.android.modules.TestPermissionChecker

@RunWith(AndroidJUnit4::class)
@LargeTest
class PermissionScreenTest : KoinComponent {

	@Rule
	@JvmField
	var testRule = ManualLaunchActivityTestRule(MainActivity::class)

	private val testTestPermissionChecker: TestPermissionChecker by inject()

	@Before
	fun setUp() {
		testTestPermissionChecker.isLocationGrantedRelay.accept(false)
		testRule.launchActivity()
	}

	@Test
	fun clickingBackFinishesActivity() {
		pressBackUnconditionally()
		testRule.activity.verifyIsFinishing()
	}

	@Test
	fun allowingPermissionOpensMainScreen() {
		PermissionScreen.grantButton.click()
		allowPermission()
		MainScreen.isDisplayed()
	}

	@Test
	fun denyingPermissionShowsDialogs() {
		PermissionScreen {
			grantButton.click()
			denyPermission()
			locationPermissionRequiredDialog.isDisplayed()
			okButton.click()
			grantButton.click()
			denyPermissionForever()
			permissionDeniedDialog.isDisplayed()
		}
	}
}
