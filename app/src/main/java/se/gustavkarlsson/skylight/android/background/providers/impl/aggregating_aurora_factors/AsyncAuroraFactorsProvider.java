package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;

import org.threeten.bp.Clock;

import javax.inject.Inject;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.skylight.android.background.providers.DarknessProvider;
import se.gustavkarlsson.skylight.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.skylight.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.skylight.android.background.providers.VisibilityProvider;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

@Reusable
public class AsyncAuroraFactorsProvider implements AuroraFactorsProvider {
	private final GeomagActivityProvider geomagActivityProvider;
	private final VisibilityProvider visibilityProvider;
	private final DarknessProvider darknessProvider;
	private final GeomagLocationProvider geomagLocationProvider;
	private final ErrorHandlingExecutorService executorService;
	private final Clock clock;

	@Inject
	AsyncAuroraFactorsProvider(GeomagActivityProvider geomagActivityProvider, VisibilityProvider visibilityProvider, DarknessProvider darknessProvider, GeomagLocationProvider geomagLocationProvider, ErrorHandlingExecutorService executorService, Clock clock) {
		this.geomagActivityProvider = geomagActivityProvider;
		this.visibilityProvider = visibilityProvider;
		this.darknessProvider = darknessProvider;
		this.geomagLocationProvider = geomagLocationProvider;
		this.executorService = executorService;
		this.clock = clock;
	}

	@Override
	public AuroraFactors getAuroraFactors(Location location, long timeoutMillis) {
		GetGeomagActivity getGeomagActivity = new GetGeomagActivity(geomagActivityProvider);
		GetGeomagLocation getGeomagLocation = new GetGeomagLocation(geomagLocationProvider, location);
		GetDarkness getDarkness = new GetDarkness(darknessProvider, location, clock.millis());
		GetVisibility getVisibility = new GetVisibility(visibilityProvider, location);

		ErrorHandlingFuture<GeomagActivity> geomagActivityErrorHandlingFuture = executorService.execute(getGeomagActivity, timeoutMillis);
		ErrorHandlingFuture<GeomagLocation> geomagLocationErrorHandlingFuture = executorService.execute(getGeomagLocation, timeoutMillis);
		ErrorHandlingFuture<Darkness> darknessErrorHandlingFuture = executorService.execute(getDarkness, timeoutMillis);
		ErrorHandlingFuture<Visibility> visibilityErrorHandlingFuture = executorService.execute(getVisibility, timeoutMillis);

		GeomagActivity geomagActivity = geomagActivityErrorHandlingFuture.get();
		GeomagLocation geomagLocation = geomagLocationErrorHandlingFuture.get();
		Darkness darkness = darknessErrorHandlingFuture.get();
		Visibility visibility = visibilityErrorHandlingFuture.get();
		return new AuroraFactors(geomagActivity, geomagLocation, darkness, visibility);
	}
}
