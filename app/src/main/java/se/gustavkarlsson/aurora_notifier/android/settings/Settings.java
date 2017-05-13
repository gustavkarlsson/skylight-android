package se.gustavkarlsson.aurora_notifier.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import se.gustavkarlsson.aurora_notifier.android.R;

public class Settings {
	private final Context context;
	private final SharedPreferences defaultPreferences;

	public Settings(Context context) {
		this.context = context;
		this.defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public boolean isEnableNotifications() {
		String key = context.getString(R.string.pref_notifications_key);
		boolean defaultValue = context.getResources().getBoolean(R.bool.pref_notifications_default);
		return defaultPreferences.getBoolean(key, defaultValue);
	}

}
