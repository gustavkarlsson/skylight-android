package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.cache.LastReportCache;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.observers.ObservableData;

@Module
public abstract class LatestAuroraReportObservableModule {
	public static final String LATEST_NAME = "Latest";

	@Provides
	@Singleton
	@Named(LATEST_NAME)
	static ObservableData<AuroraReport> provideLatestAuroraReportObservable(LastReportCache cache) {
		AuroraReport report = cache.get();
		if (report == null) {
			report = AuroraReport.createFallback();
		}
		return new ObservableData<>(report);
	}
}
