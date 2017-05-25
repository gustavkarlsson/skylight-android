package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
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
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newScheduledThreadPool;

public class AggregatingAuroraFactorsProvider implements AuroraFactorsProvider {
	private static final String TAG = AggregatingAuroraFactorsProvider.class.getSimpleName();

	private static final ListeningScheduledExecutorService EXECUTOR_SERVICE = listeningDecorator(newScheduledThreadPool(10));
	private final GeomagActivityProvider geomagActivityProvider;
	private final VisibilityProvider visibilityProvider;
	private final DarknessProvider darknessProvider;
	private final GeomagLocationProvider geomagLocationProvider;

	@Inject
	AggregatingAuroraFactorsProvider(GeomagActivityProvider geomagActivityProvider, VisibilityProvider visibilityProvider, DarknessProvider darknessProvider, GeomagLocationProvider geomagLocationProvider) {
		this.geomagActivityProvider = geomagActivityProvider;
		this.visibilityProvider = visibilityProvider;
		this.darknessProvider = darknessProvider;
		this.geomagLocationProvider = geomagLocationProvider;
	}

	@Override
	public AuroraFactors getAuroraFactors(Location location, long timeoutMillis) {
		GetGeomagActivity getGeomagActivity = new GetGeomagActivity(geomagActivityProvider);
		GetVisibility getVisibility = new GetVisibility(visibilityProvider, location);
		GetDarkness getDarkness = new GetDarkness(darknessProvider, location, System.currentTimeMillis());
		GetGeomagLocation getGeomagLocation = new GetGeomagLocation(geomagLocationProvider, location);

		ListenableFuture<GeomagActivity> geomagActivityFuture = executeWithDefault(getGeomagActivity, timeoutMillis);
		ListenableFuture<Visibility> visibilityFuture = executeWithDefault(getVisibility, timeoutMillis);
		ListenableFuture<Darkness> darknessFuture = executeWithDefault(getDarkness, timeoutMillis);
		ListenableFuture<GeomagLocation> geomagLocationFuture = executeWithDefault(getGeomagLocation, timeoutMillis);

		try {
			GeomagActivity geomagActivity = geomagActivityFuture.get();
			Visibility visibility = visibilityFuture.get();
			Darkness darkness = darknessFuture.get();
			GeomagLocation geomagLocation = geomagLocationFuture.get();
			return new AuroraFactors(geomagActivity, geomagLocation, darkness, visibility);
		} catch (InterruptedException | ExecutionException | CancellationException e) {
			throw new UserFriendlyException(R.string.error_unknown_update_error, e);
		}
	}

	private <T> ListenableFuture<T> executeWithDefault(DefaultingCallable<T> callable, long timeoutMillis) {
		ListenableFuture<T> geomagActivityFuture = EXECUTOR_SERVICE.submit(callable);
		geomagActivityFuture = Futures.withTimeout(geomagActivityFuture, timeoutMillis, TimeUnit.MILLISECONDS, EXECUTOR_SERVICE);
		geomagActivityFuture = Futures.catching(geomagActivityFuture, Exception.class, e -> {
			Log.w(TAG, "Task failed", e);
			return callable.getDefault();
		});
		return geomagActivityFuture;
	}
}
