package se.gustavkarlsson.skylight.android.gui.activities.main

import android.arch.lifecycle.LifecycleObserver
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsFragment


class MainActivity : AuroraRequirementsCheckingActivity(), LifecycleObserver {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		ensureRequirementsMet()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_settings -> {

				val fragment = SettingsFragment()
				supportFragmentManager.beginTransaction()
					.replace(R.id.fragmentContainer, fragment)
					.addToBackStack(fragment.javaClass.simpleName)
					.commit()
				true
				//startActivity<SettingsActivity>(); true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onRequirementsMet() = Unit
}
