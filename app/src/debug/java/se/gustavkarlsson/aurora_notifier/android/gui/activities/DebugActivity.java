package se.gustavkarlsson.aurora_notifier.android.gui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Map;

import se.gustavkarlsson.aurora_notifier.android.R;

public class DebugActivity extends AppCompatActivity {
	private static final String TAG = DebugActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new DebugFragment())
				.commit();
	}

	public static class DebugFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
		private static final String TAG = DebugFragment.class.getSimpleName();

		@Override
		public void onCreate(Bundle savedInstanceState) {
			Log.v(TAG, "onCreate");
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.debug_preferences);

			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onResume() {
			super.onResume();
			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
				Preference preference = getPreferenceScreen().getPreference(i);
				if (preference instanceof PreferenceGroup) {
					PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
					for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
						Preference singlePref = preferenceGroup.getPreference(j);
						updatePreference(singlePref, singlePref.getKey());
					}
				} else {
					updatePreference(preference, preference.getKey());
				}
			}
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			updatePreference(findPreference(key), key);
		}

		private void updatePreference(Preference preference, String key) {
			if (preference == null) {
				return;
			}
			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				listPreference.setSummary(listPreference.getEntry());
				return;
			}
			SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
			Map<String, ?> all = sharedPrefs.getAll();
			preference.setSummary(String.valueOf(all.get(key)));
		}
	}
}
