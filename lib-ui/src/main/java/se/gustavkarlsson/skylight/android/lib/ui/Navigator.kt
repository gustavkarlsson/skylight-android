package se.gustavkarlsson.skylight.android.lib.ui

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

class Navigator(
	private val fragmentManager: FragmentManager,
	@IdRes private val containerId: Int,
	private val destinationRegistry: DestinationRegistry
) {

	fun navigate(id: String, addToBackStack: Boolean) {
		val targetFragment = getTargetFragment(id) ?: run {
			Timber.w("No target for navigation id: $id")
			return
		}

		fragmentManager.beginTransaction().apply {
			setCustomAnimations(
				R.anim.slide_in_right,
				R.anim.slide_out_left,
				R.anim.slide_in_left,
				R.anim.slide_out_right
			)
			if (addToBackStack) {
				addToBackStack(null)
			}
			replace(containerId, targetFragment)
		}.commit()
	}

	val backStackSize
		get() = fragmentManager.backStackEntryCount

	fun goBack() = fragmentManager.popBackStack()

	fun onBackPressed(): Boolean {
		val topFragment = fragmentManager.fragments.lastOrNull()
		return topFragment is BackButtonHandler && topFragment.onBackPressed()
	}

	private fun getTargetFragment(id: String): Fragment? =
		destinationRegistry.destinations.asSequence()
			.mapNotNull { it.createFragment(id) }
			.firstOrNull()
}
