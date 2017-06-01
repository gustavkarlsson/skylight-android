package se.gustavkarlsson.skylight.android.dagger.modules;

import org.threeten.bp.LocalTime;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import se.gustavkarlsson.skylight.android.notifications.ReportOutdatedEvaluator;

@Module
public abstract class NotificationOutdatedModule {

	@Provides
	@Reusable
	static ReportOutdatedEvaluator provideReportOutdatedEvaluator() {
		return new ReportOutdatedEvaluator(LocalTime.NOON);
	}

}
