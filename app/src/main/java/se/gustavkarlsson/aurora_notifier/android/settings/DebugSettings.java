package se.gustavkarlsson.aurora_notifier.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import se.gustavkarlsson.aurora_notifier.android.R;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class DebugSettings {

	private final Context context;
	private final SharedPreferences defaultPreferences;

	public DebugSettings(Context context) {
		this.context = context;
		this.defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public boolean isOverrideValues() {
		String key = context.getString(R.string.pref_override_values_key);
		boolean defaultValue = context.getResources().getBoolean(R.bool.pref_override_values_default);
		return defaultPreferences.getBoolean(key, defaultValue);
	}

	public float getKpIndex() {
		String key = context.getString(R.string.pref_kp_index_key);
		return parseFloat(defaultPreferences.getString(key, "0"));
	}

	public float getDegreesFromClosestPole() {
		String key = context.getString(R.string.pref_geomagnetic_location_key);
		return parseFloat(defaultPreferences.getString(key, "0"));
	}

	public float getSunZenithAngle() {
		String key = context.getString(R.string.pref_sun_position_key);
		return parseFloat(defaultPreferences.getString(key, "0"));
	}

	public int getCloudPercentage() {
		String key = context.getString(R.string.pref_cloud_percentage_key);
		return parseInt(defaultPreferences.getString(key, "0"));
	}

}
