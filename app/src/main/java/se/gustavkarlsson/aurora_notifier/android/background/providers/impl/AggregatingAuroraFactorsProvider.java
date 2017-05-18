package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Location;
import android.os.AsyncTask;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetGeomagActivityTask;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetGeomagLocationTask;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetSunPositionTask;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetWeatherTask;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;
import se.gustavkarlsson.aurora_notifier.android.util.CountdownTimer;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AggregatingAuroraFactorsProvider implements AuroraFactorsProvider {
	private static final Executor EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;
	private final GeomagActivityProvider geomagActivityProvider;
	private final WeatherProvider weatherProvider;
	private final SunPositionProvider sunPositionProvider;
	private final GeomagLocationProvider geomagLocationProvider;

	@Inject
	AggregatingAuroraFactorsProvider(GeomagActivityProvider geomagActivityProvider, WeatherProvider weatherProvider, SunPositionProvider sunPositionProvider, GeomagLocationProvider geomagLocationProvider) {
		this.geomagActivityProvider = geomagActivityProvider;
		this.weatherProvider = weatherProvider;
		this.sunPositionProvider = sunPositionProvider;
		this.geomagLocationProvider = geomagLocationProvider;
	}

	@Override
	public AuroraFactors getAuroraFactors(Location location, long timeoutMillis) {
		CountdownTimer timeoutTimer = CountdownTimer.start(timeoutMillis);
		GetGeomagActivityTask getGeomagActivityTask = new GetGeomagActivityTask(geomagActivityProvider);
		GetWeatherTask getWeatherTask = new GetWeatherTask(weatherProvider, location);
		GetSunPositionTask getSunPositionTask = new GetSunPositionTask(sunPositionProvider, location, System.currentTimeMillis());
		GetGeomagLocationTask getGeomagLocationTask = new GetGeomagLocationTask(geomagLocationProvider, location);

		EXECUTOR.execute(getGeomagActivityTask);
		EXECUTOR.execute(getWeatherTask);
		EXECUTOR.execute(getSunPositionTask);
		EXECUTOR.execute(getGeomagLocationTask);

		try {
			GeomagActivity geomagActivity = getGeomagActivityTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			Weather weather = getWeatherTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			SunPosition sunPosition = getSunPositionTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			GeomagLocation geomagLocation = getGeomagLocationTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			return new AuroraFactors(geomagActivity, geomagLocation, sunPosition, weather);
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
