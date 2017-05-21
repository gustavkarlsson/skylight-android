package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.os.AsyncTask;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

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
import se.gustavkarlsson.aurora_notifier.android.util.CountdownTimer;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AggregatingAuroraFactorsProvider implements AuroraFactorsProvider {
	private static final Executor EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;
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
		CountdownTimer timeoutTimer = CountdownTimer.start(timeoutMillis);
		GetGeomagActivityTask getGeomagActivityTask = new GetGeomagActivityTask(geomagActivityProvider);
		GetVisibilityTask getVisibilityTask = new GetVisibilityTask(visibilityProvider, location);
		GetDarknessTask getDarknessTask = new GetDarknessTask(darknessProvider, location, System.currentTimeMillis());
		GetGeomagLocationTask getGeomagLocationTask = new GetGeomagLocationTask(geomagLocationProvider, location);

		EXECUTOR.execute(getGeomagActivityTask);
		EXECUTOR.execute(getVisibilityTask);
		EXECUTOR.execute(getDarknessTask);
		EXECUTOR.execute(getGeomagLocationTask);

		try {
			GeomagActivity geomagActivity = getGeomagActivityTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			Visibility visibility = getVisibilityTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			Darkness darkness = getDarknessTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			GeomagLocation geomagLocation = getGeomagLocationTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			return new AuroraFactors(geomagActivity, geomagLocation, darkness, visibility);
		} catch (TimeoutException e) {
			throw new UserFriendlyException(R.string.error_updating_took_too_long, "Getting aurora data timed out after " + timeoutMillis + "ms", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof UserFriendlyException) {
				throw (UserFriendlyException) cause;
			}
			throw new UserFriendlyException(R.string.error_unknown_update_error, cause);
		} catch (InterruptedException | CancellationException e) {
			throw new UserFriendlyException(R.string.error_unknown_update_error, e);
		}
	}
}
