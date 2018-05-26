package se.gustavkarlsson.skylight.android.gui.activities.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import se.gustavkarlsson.skylight.android.R

class SettingsFragment : PreferenceFragmentCompat() {

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		addPreferencesFromResource(R.xml.preferences)
	}
}
