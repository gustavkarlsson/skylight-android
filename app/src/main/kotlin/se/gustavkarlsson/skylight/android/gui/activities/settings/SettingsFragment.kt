package se.gustavkarlsson.skylight.android.gui.activities.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.background.UpdateScheduler

class SettingsFragment : PreferenceFragment() {

	private lateinit var notificationsChangedListener: NotificationsChangedListener

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val updateScheduler = Skylight.applicationComponent.getUpdateScheduler()
		val notificationsKey = resources.getString(R.string.pref_notifications_key)
		notificationsChangedListener = NotificationsChangedListener(updateScheduler, notificationsKey)
		preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(notificationsChangedListener)
		addPreferencesFromResource(R.xml.preferences)
		// TODO Daggerify
	}

	override fun onDestroy() {
		preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(notificationsChangedListener)
		super.onDestroy()
	}
}

private class NotificationsChangedListener(
		private val updateScheduler: UpdateScheduler,
		private val notificationsKey: String
) : SharedPreferences.OnSharedPreferenceChangeListener {

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
		if (key == this.notificationsKey) {
			updateScheduler.setupBackgroundUpdates()
		}
	}
}
