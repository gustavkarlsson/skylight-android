package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.skylight.android.settings.DebugSettings;
import se.gustavkarlsson.skylight.android.settings.SharedPreferencesDebugSettings;

@Module
public abstract class DebugSettingsModule {

	// Published
	@Binds
	@Reusable
	abstract DebugSettings bindSettings(SharedPreferencesDebugSettings sharedPreferencesDebugSettings);
}
