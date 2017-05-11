package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Location;
import android.os.AsyncTask;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetGeomagneticLocationTask;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetSolarActivityTask;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetSunPositionTask;
import se.gustavkarlsson.aurora_notifier.android.background.tasks.GetWeatherTask;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;
import se.gustavkarlsson.aurora_notifier.android.util.CountdownTimer;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AggregatingAuroraDataProvider implements AuroraDataProvider {
	private static final Executor EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;
	private final SolarActivityProvider solarActivityProvider;
	private final WeatherProvider weatherProvider;
	private final SunPositionProvider sunPositionProvider;
	private final GeomagneticLocationProvider geomagneticLocationProvider;

	@Inject
	AggregatingAuroraDataProvider(SolarActivityProvider solarActivityProvider, WeatherProvider weatherProvider, SunPositionProvider sunPositionProvider, GeomagneticLocationProvider geomagneticLocationProvider) {
		this.solarActivityProvider = solarActivityProvider;
		this.weatherProvider = weatherProvider;
		this.sunPositionProvider = sunPositionProvider;
		this.geomagneticLocationProvider = geomagneticLocationProvider;
	}

	@Override
	public AuroraData getAuroraData(Location location, long timeoutMillis) {
		CountdownTimer timeoutTimer = CountdownTimer.start(timeoutMillis);
		GetSolarActivityTask getSolarActivityTask = new GetSolarActivityTask(solarActivityProvider);
		GetWeatherTask getWeatherTask = new GetWeatherTask(weatherProvider, location);
		GetSunPositionTask getSunPositionTask = new GetSunPositionTask(sunPositionProvider, location, System.currentTimeMillis());
		GetGeomagneticLocationTask getGeomagneticLocationTask = new GetGeomagneticLocationTask(geomagneticLocationProvider, location);

		EXECUTOR.execute(getSolarActivityTask);
		EXECUTOR.execute(getWeatherTask);
		EXECUTOR.execute(getSunPositionTask);
		EXECUTOR.execute(getGeomagneticLocationTask);

		try {
			SolarActivity solarActivity = getSolarActivityTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			Weather weather = getWeatherTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			SunPosition sunPosition = getSunPositionTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			GeomagneticLocation geomagneticLocation = getGeomagneticLocationTask.get(timeoutTimer.getRemainingTimeMillis(), MILLISECONDS);
			return new AuroraData(solarActivity, geomagneticLocation, sunPosition, weather);
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
