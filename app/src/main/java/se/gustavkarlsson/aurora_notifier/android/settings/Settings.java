package se.gustavkarlsson.aurora_notifier.android.settings;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;

public class Settings {
	private final Context context;
	private final SharedPreferences sharedPreferences;

	@Inject
	public Settings(Context context, SharedPreferences sharedPreferences) {
		this.context = context;
		this.sharedPreferences = sharedPreferences;
	}

	public boolean isEnableNotifications() {
		String key = context.getString(R.string.pref_notifications_key);
		boolean defaultValue = context.getResources().getBoolean(R.bool.setting_default_notifications_enabled);
		return sharedPreferences.getBoolean(key, defaultValue);
	}

}
