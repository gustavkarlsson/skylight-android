package se.gustavkarlsson.skylight.android.gui.screens.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity

class SettingsFragment : PreferenceFragmentCompat() {

	override fun onStart() {
		super.onStart()
		appCompatActivity!!.supportActionBar!!.show()
	}

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		addPreferencesFromResource(R.xml.preferences)
	}
}
