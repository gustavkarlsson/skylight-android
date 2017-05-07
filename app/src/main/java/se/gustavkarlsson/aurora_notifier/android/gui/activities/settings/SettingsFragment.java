package se.gustavkarlsson.aurora_notifier.android.gui.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;

public class SettingsFragment extends PreferenceFragment {
	private static final String TAG = SettingsFragment.class.getSimpleName();

	private NotificationsChangedListener notificationsChangedListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		registerNotificationsChangedListener();
	}

	private void registerNotificationsChangedListener() {
		notificationsChangedListener = new NotificationsChangedListener();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(notificationsChangedListener);
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		unregisterNotificationsChangedListener();
		super.onDestroy();
	}

	private void unregisterNotificationsChangedListener() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(notificationsChangedListener);
	}

	private class NotificationsChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {
		private final String notificationsKey;

		private NotificationsChangedListener() {
			this.notificationsKey = getResources().getString(R.string.pref_notifications_key);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals(this.notificationsKey)) {
				UpdateScheduler.setupUpdateScheduling(getActivity());
			}
		}
	}
}
