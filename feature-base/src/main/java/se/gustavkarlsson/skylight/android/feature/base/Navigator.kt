package se.gustavkarlsson.skylight.android.feature.base

import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

class Navigator(
	private val fragmentManager: FragmentManager,
	@IdRes private val containerId: Int,
	private val destinationRegistry: DestinationRegistry
) {

	fun navigate(id: String) {
		val destinations = destinationRegistry.destinations
		val (targetDestination, targetFragment) = destinations.getTarget(id) ?: run {
			Timber.w("No target for navigation id: $id")
			return
		}
		val currentDestination = fragmentManager.getTopDestination(destinations)

		if (currentDestination.popOnLeave)
			fragmentManager.popBackStack()

		fragmentManager.beginTransaction().apply {
			getAnimations(currentDestination, targetDestination).run {
				setCustomAnimations(enter, exit, popEnter, popExit)
			}
			addToBackStack(targetDestination.name)
			replace(containerId, targetFragment)
		}.commit()
	}

	private fun List<Destination>.getTarget(id: String): Pair<Destination, Fragment>? = asSequence()
		.map { destination -> destination to destination.createFragment(id) }
		.filter { (_, fragment) -> fragment != null }
		.map { (destination, fragment) -> destination to fragment!! }
		.firstOrNull()

	private fun FragmentManager.getTopDestination(destinations: List<Destination>): Destination {
		val topIndex = backStackEntryCount - 1
		if (topIndex < 0)
			return EMPTY_DESTINATION
		val tag = getBackStackEntryAt(topIndex).name
		return destinations.first { it.name == tag }
	}

	private fun getAnimations(
		current: Destination,
		target: Destination
	): Animations {
		return when {
			!current.popOnLeave && target.popOnLeave -> {
				Animations.VERTICAL
			}
			current.popOnLeave && !target.popOnLeave -> {
				Animations.VERTICAL_INVERSE
			}
			else -> Animations.HORIZONTAL
		}
	}

	private enum class Animations(
		@AnimRes val enter: Int,
		@AnimRes val exit: Int,
		@AnimRes val popEnter: Int,
		@AnimRes val popExit: Int
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

private val EMPTY_DESTINATION =
	Destination(
		name = "empty",
		priority = Int.MIN_VALUE,
		popOnLeave = false
	) { null }
