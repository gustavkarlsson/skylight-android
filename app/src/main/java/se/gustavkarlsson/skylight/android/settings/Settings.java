package se.gustavkarlsson.skylight.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;

@Reusable
public class Settings {
	private final SharedPreferences defaultPreferences;
	private final String notificationsKey;
	private final boolean notificationsDefaultValue;
	private final String triggerLevelKey;
	private final String triggerLevelDefaultValue;

	@Inject
	Settings(Context context) {
		this.defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		notificationsKey = context.getString(R.string.pref_notifications_key);
		notificationsDefaultValue = context.getResources().getBoolean(R.bool.pref_notifications_default);
		triggerLevelKey = context.getString(R.string.pref_trigger_level_key);
		triggerLevelDefaultValue = context.getResources().getString(R.string.pref_trigger_level_default);
	}

	public boolean isEnableNotifications() {
		return defaultPreferences.getBoolean(notificationsKey, notificationsDefaultValue);
	}

	public ChanceLevel getTriggerLevel() {
		String triggerLevelRaw = defaultPreferences.getString(triggerLevelKey, triggerLevelDefaultValue);
		int triggerLevel = Integer.parseInt(triggerLevelRaw);
		return ChanceLevel.values()[triggerLevel];
	}

}
