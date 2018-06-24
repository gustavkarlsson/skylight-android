package se.gustavkarlsson.skylight.android.gui.screens.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity

class SettingsFragment : PreferenceFragmentCompat() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		appCompatActivity!!.supportActionBar!!.show()
	}

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		addPreferencesFromResource(R.xml.preferences)
	}
}
