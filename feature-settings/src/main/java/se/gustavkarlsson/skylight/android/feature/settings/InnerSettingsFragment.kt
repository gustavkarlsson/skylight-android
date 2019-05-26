package se.gustavkarlsson.skylight.android.feature.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.ext.android.get

internal class InnerSettingsFragment : PreferenceFragmentCompat() {

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		get<List<Int>>("preferences").forEach(::addPreferencesFromResource)
	}
}
