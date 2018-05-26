package se.gustavkarlsson.skylight.android.gui.activities.main

import android.arch.lifecycle.LifecycleObserver
import android.os.Bundle
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.findNavController
import se.gustavkarlsson.skylight.android.extensions.setupActionBarWithNavController
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity


class MainActivity : AuroraRequirementsCheckingActivity(), LifecycleObserver {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setupActionBarWithNavController(findNavController())
		ensureRequirementsMet()
	}

	override fun onSupportNavigateUp(): Boolean =
		findNavController().navigateUp()

	private fun findNavController() = findNavController(R.id.mainNavHost)

	override fun onRequirementsMet() = Unit
}
