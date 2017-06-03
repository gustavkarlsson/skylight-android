package se.gustavkarlsson.skylight.android.dagger.components;

import org.threeten.bp.Clock;

import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateJob;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.EvaluationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.AuroraReportCacheModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.AuroraReportModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.ClockModule;

@Component(modules = {
		AuroraReportCacheModule.class,
		EvaluationModule.class,
		AuroraReportModule.class,
		ClockModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	AuroraReportProvider getAuroraReportProvider();
	UpdateJob getUpdateJob();
	UpdateScheduler getUpdateScheduler();
	Clock getClock();

	MainActivityComponent getMainActivityComponent(ActivityModule activityModule);
}
