package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import org.threeten.bp.Duration;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

import static se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_SCHEDULER_FLEX_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_SCHEDULER_INTERVAL_NAME;

@Module
public abstract class UpdateSchedulerModule {

	// Published
	@Provides
	@Reusable
	@Named(UPDATE_SCHEDULER_INTERVAL_NAME)
	static Duration provideUpdateSchedulerInterval() {
		return Duration.ofMinutes(20);
	}

	// Published
	@Provides
	@Reusable
	@Named(UPDATE_SCHEDULER_FLEX_NAME)
	static Duration provideUpdateSchedulerFlex() {
		return Duration.ofMinutes(10);
	}

}
