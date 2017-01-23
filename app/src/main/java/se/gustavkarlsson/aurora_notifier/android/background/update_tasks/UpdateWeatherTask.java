package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.location.Location;
import android.util.Log;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.Weather;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class UpdateWeatherTask extends RealmEnclosedAsyncTask<Object, Object, Object> {
	private static final String TAG = UpdateSunPositionTask.class.getSimpleName();

	private final WeatherProvider provider;
	private final Location location;

	public UpdateWeatherTask(WeatherProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	protected Object doInBackgroundWithRealm(Realm realm, Object... params) {
		try {
			Log.i(TAG, "Getting weather...");
			final Timestamped<? extends Weather> weather = provider.getWeather(location.getLatitude(), location.getLongitude());
			Log.d(TAG, "Weather is:  " + weather);

			Log.d(TAG, "Looking up weather from realm...");
			final RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
			Log.d(TAG, "Realm weather is:  " + realmWeather);

			Log.d(TAG, "Storing weather in realm");
			realm.executeTransaction(r -> {
                realmWeather.setCloudPercentage(weather.getValue().getCloudPercentage());
                realmWeather.setTimestamp(weather.getTimestamp());
            });
			Log.i(TAG, "Updated weather in realm");
		} catch (ProviderException e) {
			e.printStackTrace();
		}
		return null;
	}
}
