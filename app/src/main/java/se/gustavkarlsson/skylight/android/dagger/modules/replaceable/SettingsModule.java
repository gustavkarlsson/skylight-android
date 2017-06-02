package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.skylight.android.settings.Settings;
import se.gustavkarlsson.skylight.android.settings.SharedPreferencesSettings;

@Module
public abstract class SettingsModule {

	// Published
	@Binds
	@Reusable
	abstract Settings bindSettings(SharedPreferencesSettings sharedPreferencesSettings);
}
