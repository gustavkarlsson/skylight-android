package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.settings.Settings;

@Module
public abstract class SettingsModule {

	@Provides
	@Reusable
	static Settings provideSettings(Context context, SharedPreferences sharedPreferences) {
		return new Settings(context, sharedPreferences);
	}

	@Provides
	@Reusable
	static SharedPreferences provideSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

}
