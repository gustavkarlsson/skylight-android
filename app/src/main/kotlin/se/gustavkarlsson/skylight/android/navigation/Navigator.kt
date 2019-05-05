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
			popIfOnTop(currentScreen)
			setAnimation(getAnimationDirection(currentScreen, targetScreen))
		}
	}

	private fun NavOptionsBuilder.popIfOnTop(screen: Screen) {
		if (screen.alwaysOnTop) {
			popUpTo(screen.id) { inclusive = true }
		}
	}

	private fun getAnimationDirection(
		currentScreen: Screen,
		targetScreen: Screen
	): AnimationDirection {
		return when {
			!currentScreen.alwaysOnTop && targetScreen.alwaysOnTop -> {
				AnimationDirection.VERTICAL
			}
			currentScreen.alwaysOnTop && !targetScreen.alwaysOnTop -> {
				AnimationDirection.VERTICAL_INVERSE
			}
			else -> AnimationDirection.HORIZONTAL
		}
	}

	private fun NavOptionsBuilder.setAnimation(direction: AnimationDirection) {
		anim {
			direction.let {
				enter = it.enterAnim
				exit = it.exitAnim
				popEnter = it.popEnterAnim
				popExit = it.popExitAnim
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
