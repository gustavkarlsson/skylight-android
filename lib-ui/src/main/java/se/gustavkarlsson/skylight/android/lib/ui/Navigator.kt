package se.gustavkarlsson.skylight.android.lib.ui

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

	fun navigate(vararg ids: String) {
		val id = ids[0] // FIXME handle multiple IDs
		val destinations = destinationRegistry.destinations
		val (targetDestination, targetFragment) = destinations.getTarget(id) ?: run {
			Timber.w("No target for navigation id: $id")
			return
		}
		val currentDestination = fragmentManager.getTopDestination(destinations)

		fragmentManager.beginTransaction().apply {
			getAnimations(currentDestination, targetDestination).run {
				setCustomAnimations(enter, exit, popEnter, popExit)
			}
			if (currentDestination.addToBackStack) {
				addToBackStack(null)
			}
			replace(containerId, targetFragment, targetDestination.name)
		}.commit()
	}

	fun goBack() = fragmentManager.popBackStack()

	fun onBackPressed(): Boolean {
		val topFragment = fragmentManager.fragments.lastOrNull()
		return topFragment is BackButtonHandler && topFragment.onBackPressed()
	}

	private fun List<Destination>.getTarget(id: String): Pair<Destination, Fragment>? = asSequence()
		.map { destination -> destination to destination.createFragment(id) }
		.filter { (_, fragment) -> fragment != null }
		.map { (destination, fragment) -> destination to fragment!! }
		.firstOrNull()

	private fun FragmentManager.getTopDestination(destinations: List<Destination>): Destination {
		val tag = fragments.lastOrNull()?.tag ?: return EMPTY_DESTINATION
		return destinations.first { it.name == tag }
	}

	private fun getAnimations(
		current: Destination,
		target: Destination
	): Animations = when {
		!current.addToBackStack && target.addToBackStack -> {
			Animations.VERTICAL
		}
		current.addToBackStack && !target.addToBackStack -> {
			Animations.VERTICAL_INVERSE
		}
		else -> Animations.HORIZONTAL
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
		addToBackStack = false
	) { null }
