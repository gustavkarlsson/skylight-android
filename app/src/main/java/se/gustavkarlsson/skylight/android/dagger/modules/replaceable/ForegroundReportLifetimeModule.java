package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import org.threeten.bp.Duration;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

import static se.gustavkarlsson.skylight.android.dagger.Names.FOREGROUND_REPORT_LIFETIME_NAME;

@Module
public abstract class ForegroundReportLifetimeModule {

	// Published
	@Provides
	@Reusable
	@Named(FOREGROUND_REPORT_LIFETIME_NAME)
	static Duration provideForegroundReportLifetime() {
		return Duration.ofMinutes(15);
	}

}
