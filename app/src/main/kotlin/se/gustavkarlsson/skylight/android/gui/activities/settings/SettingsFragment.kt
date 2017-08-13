package se.gustavkarlsson.skylight.android.gui.activities.settings

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceFragment
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.SetUpdateSchedule
import javax.inject.Inject

class SettingsFragment : PreferenceFragment() {

	@Inject
	lateinit var setUpdateSchedule: SetUpdateSchedule

	private lateinit var notificationsChangedListener: OnSharedPreferenceChangeListener

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		(activity as SettingsActivity).component
			.getSettingsFragmentComponent()
			.inject(this)
		listenToChanges()
		addPreferencesFromResource(R.xml.preferences)
	}

	override fun onDestroy() {
		stopListeningToChanges()
		super.onDestroy()
	}

	private fun listenToChanges() {
		val notificationsKey = resources.getString(R.string.pref_notifications_key)
		notificationsChangedListener = OnSharedPreferenceChangeListener { _, key -> if (key == notificationsKey) setUpdateSchedule() }
		preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(notificationsChangedListener)
	}

	private fun stopListeningToChanges() {
		preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(notificationsChangedListener)
	}
}
