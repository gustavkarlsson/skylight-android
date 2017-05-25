package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.DarknessProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.VisibilityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

import static se.gustavkarlsson.aurora_notifier.android.dagger.modules.ExecutorModule.CACHED_THREAD_POOL;

public class AggregatingAuroraFactorsProvider implements AuroraFactorsProvider {
	private final GeomagActivityProvider geomagActivityProvider;
	private final VisibilityProvider visibilityProvider;
	private final DarknessProvider darknessProvider;
	private final GeomagLocationProvider geomagLocationProvider;
	private final ErrorHandlingExecutorService cachedThreadPool;

	@Inject
	AggregatingAuroraFactorsProvider(GeomagActivityProvider geomagActivityProvider, VisibilityProvider visibilityProvider, DarknessProvider darknessProvider, GeomagLocationProvider geomagLocationProvider, @Named(CACHED_THREAD_POOL) ExecutorService cachedThreadPool) {
		this.geomagActivityProvider = geomagActivityProvider;
		this.visibilityProvider = visibilityProvider;
		this.darknessProvider = darknessProvider;
		this.geomagLocationProvider = geomagLocationProvider;
		this.cachedThreadPool = new ErrorHandlingExecutorService(cachedThreadPool);
	}

	@Override
	public AuroraFactors getAuroraFactors(Location location, long timeoutMillis) {
		GetGeomagActivity getGeomagActivity = new GetGeomagActivity(geomagActivityProvider);
		GetGeomagLocation getGeomagLocation = new GetGeomagLocation(geomagLocationProvider, location);
		GetDarkness getDarkness = new GetDarkness(darknessProvider, location, System.currentTimeMillis());
		GetVisibility getVisibility = new GetVisibility(visibilityProvider, location);

		ErrorHandlingFuture<GeomagActivity> geomagActivityErrorHandlingFuture = cachedThreadPool.execute(getGeomagActivity, timeoutMillis);
		ErrorHandlingFuture<GeomagLocation> geomagLocationErrorHandlingFuture = cachedThreadPool.execute(getGeomagLocation, timeoutMillis);
		ErrorHandlingFuture<Darkness> darknessErrorHandlingFuture = cachedThreadPool.execute(getDarkness, timeoutMillis);
		ErrorHandlingFuture<Visibility> visibilityErrorHandlingFuture = cachedThreadPool.execute(getVisibility, timeoutMillis);

		GeomagActivity geomagActivity = geomagActivityErrorHandlingFuture.get();
		GeomagLocation geomagLocation = geomagLocationErrorHandlingFuture.get();
		Darkness darkness = darknessErrorHandlingFuture.get();
		Visibility visibility = visibilityErrorHandlingFuture.get();
		return new AuroraFactors(geomagActivity, geomagLocation, darkness, visibility);
	}
}
