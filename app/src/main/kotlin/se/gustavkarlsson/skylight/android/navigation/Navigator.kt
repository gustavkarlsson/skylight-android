package se.gustavkarlsson.skylight.android.navigation

import androidx.annotation.AnimRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import se.gustavkarlsson.skylight.android.R

class Navigator(private val navController: NavController) {

	fun navigate(targetScreen: Screen) {
		val currentScreen = navController.currentDestination!!.id.let(Screen.Companion::fromId)
		if (targetScreen == currentScreen) return

		navController.navigate(targetScreen.id, null, getNavOptions(currentScreen, targetScreen))
	}

	private fun getNavOptions(currentScreen: Screen, targetScreen: Screen): NavOptions {
		return navOptions {
			setPopUpTo(currentScreen, targetScreen)
			val direction = when {
				!currentScreen.alwaysOnTop && targetScreen.alwaysOnTop -> {
					AnimationDirection.VERTICAL
				}
				currentScreen.alwaysOnTop && !targetScreen.alwaysOnTop -> {
					AnimationDirection.VERTICAL_INVERSE
				}
				else -> AnimationDirection.HORIZONTAL
			}
			anim {
				direction.run {
					enter = enterAnim
					exit = exitAnim
					popEnter = popEnterAnim
					popExit = popExitAnim
				}
			}
		}
	}

	private fun NavOptionsBuilder.setPopUpTo(currentScreen: Screen, targetScreen: Screen) {
		if (currentScreen.alwaysOnTop || targetScreen.alwaysOnTop) {
			popUpTo(currentScreen.id) {
				inclusive = true
			}
		}
	}

	private enum class AnimationDirection(
		@AnimRes val enterAnim: Int,
		@AnimRes val exitAnim: Int,
		@AnimRes val popEnterAnim: Int,
		@AnimRes val popExitAnim: Int
	) {
		HORIZONTAL(
			R.anim.slide_in_right,
			R.anim.slide_out_left,
			R.anim.slide_in_left,
			R.anim.slide_out_right
		),
		VERTICAL(
			R.anim.slide_in_down,
			R.anim.slide_out_up,
			R.anim.slide_in_up,
			R.anim.slide_out_down
		),
		VERTICAL_INVERSE(
			R.anim.slide_in_up,
			R.anim.slide_out_down,
			R.anim.slide_in_down,
			R.anim.slide_out_up
		)
	}
}
