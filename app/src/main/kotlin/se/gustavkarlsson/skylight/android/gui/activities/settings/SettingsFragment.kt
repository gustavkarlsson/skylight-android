package se.gustavkarlsson.skylight.android.gui.activities.settings

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceFragment
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.Settings
import javax.inject.Inject

class SettingsFragment : PreferenceFragment() {

	@Inject
	lateinit var updateScheduler: Scheduler

	@Inject
	lateinit var settings: Settings

	private lateinit var notificationsChangedListener: OnSharedPreferenceChangeListener

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		(activity as SettingsActivity).component
			.getSettingsFragmentComponent()
			.inject(this)
		val notificationsKey = resources.getString(R.string.pref_notifications_key)
		notificationsChangedListener = OnSharedPreferenceChangeListener { _, key ->
			if (key == notificationsKey) {
				if (settings.isEnableNotifications) {
					updateScheduler.schedule()
				} else {
					updateScheduler.unschedule()
				}
			}
		}
		preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(notificationsChangedListener)
		addPreferencesFromResource(R.xml.preferences)
	}

	override fun onDestroy() {
		preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(notificationsChangedListener)
		super.onDestroy()
	}
}
