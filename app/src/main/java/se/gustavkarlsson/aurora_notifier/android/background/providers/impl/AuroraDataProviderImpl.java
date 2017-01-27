package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Location;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import java8.util.J8Arrays;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateGeomagneticLocationTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateSolarActivityTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateSunPositionTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateWeatherTask;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;
import se.gustavkarlsson.aurora_notifier.android.util.Alarm;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AuroraDataProviderImpl implements AuroraDataProvider {
	private final SolarActivityProvider solarActivityProvider;
	private final WeatherProvider weatherProvider;
	private final SunPositionProvider sunPositionProvider;
	private final GeomagneticLocationProvider geomagneticLocationProvider;

	public AuroraDataProviderImpl(SolarActivityProvider solarActivityProvider, WeatherProvider weatherProvider, SunPositionProvider sunPositionProvider, GeomagneticLocationProvider geomagneticLocationProvider) {
		this.solarActivityProvider = solarActivityProvider;
		this.weatherProvider = weatherProvider;
		this.sunPositionProvider = sunPositionProvider;
		this.geomagneticLocationProvider = geomagneticLocationProvider;
	}

	@Override
	public AuroraData getAuroraData(long timeoutMillis, Location location) {
		Alarm timeoutAlarm = Alarm.start(timeoutMillis);
		UpdateSolarActivityTask updateSolarActivityTask = new UpdateSolarActivityTask(solarActivityProvider);
		UpdateWeatherTask updateWeatherTask = new UpdateWeatherTask(weatherProvider, location);
		UpdateSunPositionTask updateSunPositionTask = new UpdateSunPositionTask(sunPositionProvider, location, System.currentTimeMillis());
		UpdateGeomagneticLocationTask updateGeomagneticLocationTask = new UpdateGeomagneticLocationTask(geomagneticLocationProvider, location);

		executeInParallel(
				updateSolarActivityTask,
				updateWeatherTask,
				updateSunPositionTask,
				updateGeomagneticLocationTask);

		try {
			SolarActivity solarActivity = updateSolarActivityTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			Weather weather = updateWeatherTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			SunPosition sunPosition = updateSunPositionTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			GeomagneticLocation geomagneticLocation = updateGeomagneticLocationTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			return new AuroraData(solarActivity, geomagneticLocation, sunPosition, weather);
		} catch (TimeoutException e) {
			throw new UserFriendlyException(R.string.update_took_too_long, "Getting aurora data timed out after " + timeoutMillis + "ms", e);
		} catch (InterruptedException | ExecutionException e) {
			throw new UserFriendlyException(R.string.unknown_update_error, e);
		}
	}

	private static void executeInParallel(AsyncTask... tasks) {
		Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
		J8Arrays.stream(tasks)
				.forEach(task -> task.executeOnExecutor(executor));
	}
}
