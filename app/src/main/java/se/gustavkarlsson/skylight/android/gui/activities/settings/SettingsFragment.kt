package se.gustavkarlsson.skylight.android.gui.activities.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.background.UpdateScheduler

class SettingsFragment : PreferenceFragment() {

    private lateinit var notificationsChangedListener: NotificationsChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        val updateScheduler = Skylight.applicationComponent.getUpdateScheduler()
		val notificationsKey = resources.getString(R.string.pref_notifications_key)
		notificationsChangedListener = NotificationsChangedListener(updateScheduler, notificationsKey)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(notificationsChangedListener)
        addPreferencesFromResource(R.xml.preferences)
        // TODO Daggerify
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(notificationsChangedListener)
        super.onDestroy()
    }

    companion object {
        private val TAG = SettingsFragment::class.java.simpleName
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
