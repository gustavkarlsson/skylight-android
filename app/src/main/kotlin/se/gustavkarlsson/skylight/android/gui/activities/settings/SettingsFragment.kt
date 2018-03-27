package se.gustavkarlsson.skylight.android.gui.activities.settings

import android.os.Bundle
import android.preference.PreferenceFragment
import se.gustavkarlsson.skylight.android.R

class SettingsFragment : PreferenceFragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(R.xml.preferences)
	}
}
