package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.settings.Settings;

@Module
public abstract class UpdateSchedulerModule {

	@Provides
	@Singleton
	static UpdateScheduler provideUpdateScheduler(Context context, Settings settings) {
		return new UpdateScheduler(context, settings);
	}

}
