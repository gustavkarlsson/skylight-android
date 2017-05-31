package se.gustavkarlsson.skylight.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.R;

@Reusable
public class Settings {
	private final SharedPreferences defaultPreferences;
	private final String notificationsKey;
	private final boolean notificationsDefaultValue;

	@Inject
	Settings(Context context) {
		this.defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		notificationsKey = context.getString(R.string.pref_notifications_key);
		notificationsDefaultValue = context.getResources().getBoolean(R.bool.pref_notifications_default);
	}

	public boolean isEnableNotifications() {
		return defaultPreferences.getBoolean(notificationsKey, notificationsDefaultValue);
	}

}
