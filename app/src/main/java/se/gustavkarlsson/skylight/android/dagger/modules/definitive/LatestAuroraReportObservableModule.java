package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import org.threeten.bp.Clock;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.cache.SingletonCache;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;
import se.gustavkarlsson.skylight.android.observers.ObservableData;

import static se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME;

@Module
public abstract class LatestAuroraReportObservableModule {

	@Provides
	@Singleton
	@Named(LATEST_NAME)
	static ObservableData<AuroraReport> provideLatestAuroraReportObservable(@Named(LATEST_NAME) SingletonCache<AuroraReport> cache, Clock clock) {
		AuroraReport report = cache.getValue();
		if (report == null) {
			AuroraFactors factors = new AuroraFactors(
					new GeomagActivity(null),
					new GeomagLocation(null),
					new Darkness(null),
					new Visibility(null)
			);
			report = new AuroraReport(clock.millis(), null, factors);
		}
		return new ObservableData<>(report);
	}
}
