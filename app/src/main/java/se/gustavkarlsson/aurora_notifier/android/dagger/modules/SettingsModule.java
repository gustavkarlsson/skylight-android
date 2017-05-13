package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.settings.DebugSettings;
import se.gustavkarlsson.aurora_notifier.android.settings.Settings;

@Module
public abstract class SettingsModule {

	@Provides
	@Reusable
	static Settings provideSettings(Context context) {
		return new Settings(context);
	}

	@Provides
	@Reusable
	static DebugSettings provideDebugSettings(Context context) {
		return new DebugSettings(context);
	}

}
