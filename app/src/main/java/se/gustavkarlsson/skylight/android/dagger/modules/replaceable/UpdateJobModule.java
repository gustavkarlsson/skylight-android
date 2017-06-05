package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.app.NotificationManager;

import org.threeten.bp.Duration;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.background.UpdateJob;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.background.Updater;

import static se.gustavkarlsson.skylight.android.dagger.Names.BACKGROUND_UPDATE_TIMEOUT_NAME;

@Module
public abstract class UpdateJobModule {

	// Published
	@Provides
	static UpdateJob provideUpdateJob(NotificationManager notificationManager, UpdateScheduler updateScheduler, Updater updater, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) Duration timeout) {
		return new UpdateJob(notificationManager, updateScheduler, updater, timeout);
	}

}
