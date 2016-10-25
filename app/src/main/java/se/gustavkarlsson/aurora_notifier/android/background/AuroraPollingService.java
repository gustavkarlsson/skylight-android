package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticCoordinatesProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.KpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.Weather;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GeomagneticCoordinatesProviderImpl;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.KlausBrunnerSunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedKpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class AuroraPollingService extends WakefulIntentService {

	private static final String TAG = AuroraPollingService.class.getSimpleName();
	private static final String ACTION_UPDATE = TAG + ".UPDATE";
	private static final long UPDATE_TIMEOUT_MILLIS = 60_000;

	private KpIndexProvider kpIndexProvider;
	private WeatherProvider weatherProvider;
	private SunPositionProvider sunPositionProvider;
	private GeomagneticCoordinatesProvider geomagneticCoordinatesProvider;
	private GoogleApiClient googleApiClient;

	// Default constructor required
	public AuroraPollingService() {
		super(AuroraPollingService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		if (kpIndexProvider == null) {
			kpIndexProvider = RetrofittedKpIndexProvider.createDefault();
		}
		if (weatherProvider == null) {
			weatherProvider = RetrofittedOpenWeatherMapProvider.createDefault();
		}
		if (sunPositionProvider == null) {
			sunPositionProvider = new KlausBrunnerSunPositionProvider();
		}
		if (geomagneticCoordinatesProvider == null) {
			geomagneticCoordinatesProvider = new GeomagneticCoordinatesProviderImpl();
		}
		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(this)
					.addApi(LocationServices.API)
					.build();
		}
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		Log.v(TAG, "doWakefulWork");
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_UPDATE.equals(action)) {
				update();
			}
		}
	}

	public void update() {
		Log.v(TAG, "update");
		try {
			long startTime = System.currentTimeMillis();
			long timeLeftMillis = UPDATE_TIMEOUT_MILLIS;
			AsyncTask updateKpIndexTask = new UpdateKpIndexTask(kpIndexProvider).executeOnExecutor(THREAD_POOL_EXECUTOR);
			Log.d(TAG, "Connecting to Google Play Services...");
			ConnectionResult connectionResult = googleApiClient.blockingConnect(timeLeftMillis, TimeUnit.MILLISECONDS);
			if (connectionResult.isSuccess()) {
				Log.d(TAG, "Successfully connected to Google Play Services");
				Log.d(TAG, "Getting location");
				Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
				if (location != null) {
					Log.d(TAG, "Location is: " + location);
					AsyncTask updateWeatherTask = new UpdateWeatherTask(weatherProvider, location).executeOnExecutor(THREAD_POOL_EXECUTOR);
					AsyncTask updateSunPositionTask = new UpdateSunPositionTask(sunPositionProvider, location, System.currentTimeMillis()).executeOnExecutor(THREAD_POOL_EXECUTOR);
					AsyncTask updateGeomagneticCoordinatesTask = new UpdateGeomagneticCoordinatesTask(geomagneticCoordinatesProvider, location).executeOnExecutor(THREAD_POOL_EXECUTOR);

					timeLeftMillis = UPDATE_TIMEOUT_MILLIS - (System.currentTimeMillis() - startTime);
					updateKpIndexTask.get(timeLeftMillis, TimeUnit.MILLISECONDS);
					timeLeftMillis = UPDATE_TIMEOUT_MILLIS - (System.currentTimeMillis() - startTime);
					updateWeatherTask.get(timeLeftMillis, TimeUnit.MILLISECONDS);
					timeLeftMillis = UPDATE_TIMEOUT_MILLIS - (System.currentTimeMillis() - startTime);
					updateSunPositionTask.get(timeLeftMillis, TimeUnit.MILLISECONDS);
					timeLeftMillis = UPDATE_TIMEOUT_MILLIS - (System.currentTimeMillis() - startTime);
					updateGeomagneticCoordinatesTask.get(timeLeftMillis, TimeUnit.MILLISECONDS);
					// TODO Clean up this mess
				} else {
					Log.w(TAG, "Could not get location");
				}
			} else {
				Log.e(TAG, "Failed to connect to Google Play Services: " + connectionResult.getErrorMessage());
			}
		} catch (SecurityException e) {
			Log.e(TAG, "Location permission not given", e);
		} catch (InterruptedException e) {
			Log.e(TAG, "Task was interrupted", e);
		} catch (ExecutionException e) {
			// TODO  handle better
			Log.e(TAG, "Task threw exception", e);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} finally {
			if (googleApiClient.isConnected()) {
				Log.d(TAG, "Disconnecting from Google Play Services");
				googleApiClient.disconnect();
			}
		}
	}

	public static Intent createUpdateIntent(Context context) {
		return new Intent(ACTION_UPDATE, null, context, AuroraPollingService.class);
	}

	public static void sendUpdateRequest(Context context) {
		Intent updateIntent = createUpdateIntent(context);
		WakefulIntentService.sendWakefulWork(context, updateIntent);
	}

	private static abstract class RealmEnclosedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

		@Override
		protected final Result doInBackground(Params[] params) {
			Realm realm = Realm.getDefaultInstance();
			try {
				return doInBackgroundWithRealm(realm, params);
			} finally {
				if (!realm.isClosed()) {
					realm.close();
				}
			}
		}

		protected abstract Result doInBackgroundWithRealm(Realm realm, Params[] params);
	}

	private static class UpdateKpIndexTask extends RealmEnclosedAsyncTask<Void, Void, Void> {
		private final KpIndexProvider provider;

		UpdateKpIndexTask(KpIndexProvider provider) {
			this.provider = provider;
		}

		@Override
		protected Void doInBackgroundWithRealm(Realm realm, Void... params) {
			try {
				Log.i(TAG, "Getting KP index...");
				final Timestamped<Float> kpIndex = provider.getKpIndex();
				Log.d(TAG, "KP Index is: " + kpIndex);

				Log.d(TAG, "Looking up KP index from realm...");
				final RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
				Log.d(TAG, "Realm KP index is:  " + realmKpIndex);

				Log.d(TAG, "Storing KP index in realm");
				realm.executeTransaction(new Realm.Transaction() {
					@Override
					public void execute(Realm realm) {
						realmKpIndex.setKpIndex(kpIndex.getValue());
						realmKpIndex.setTimestamp(kpIndex.getTimestamp());
					}
				});
				Log.i(TAG, "Updated KP index in realm");
			} catch (ProviderException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class UpdateWeatherTask extends RealmEnclosedAsyncTask<Void, Void, Void> {
		private final WeatherProvider provider;
		private final Location location;

		private UpdateWeatherTask(WeatherProvider provider, Location location) {
			this.provider = provider;
			this.location = location;
		}

		@Override
		protected Void doInBackgroundWithRealm(Realm realm, Void... params) {
			try {
				Log.i(TAG, "Getting weather...");
				final Timestamped<? extends Weather> weather = provider.getWeather(location.getLatitude(), location.getLongitude());
				Log.d(TAG, "Weather is:  " + weather);

				Log.d(TAG, "Looking up weather from realm...");
				final RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
				Log.d(TAG, "Realm weather is:  " + realmWeather);

				Log.d(TAG, "Storing weather in realm");
				realm.executeTransaction(new Realm.Transaction() {
					@Override
					public void execute(Realm realm) {
						realmWeather.setCloudPercentage(weather.getValue().getCloudPercentage());
						realmWeather.setTimestamp(weather.getTimestamp());
					}
				});
				Log.i(TAG, "Updated weather in realm");
			} catch (ProviderException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class UpdateSunPositionTask extends RealmEnclosedAsyncTask<Void, Void, Void> {
		private final SunPositionProvider provider;
		private final Location location;
		private final long timeMillis;

		private UpdateSunPositionTask(SunPositionProvider provider, Location location, long timeMillis) {
			this.provider = provider;
			this.location = location;
			this.timeMillis = timeMillis;
		}

		@Override
		protected Void doInBackgroundWithRealm(Realm realm, Void... params) {
			try {
				Log.i(TAG, "Getting sun position...");
				final Timestamped<Float> zenithAngle = provider.getZenithAngle(timeMillis, location.getLatitude(), location.getLongitude());
				Log.d(TAG, "Sun position is: " + zenithAngle);

				Log.d(TAG, "Looking up sun position from realm...");
				final RealmSunPosition realmSunPosition = realm.where(RealmSunPosition.class).findFirst();
				Log.d(TAG, "Realm sun position is:  " + realmSunPosition);

				Log.d(TAG, "Storing sun position in realm");
				realm.executeTransaction(new Realm.Transaction() {
					@Override
					public void execute(Realm realm) {
						realmSunPosition.setZenithAngle(zenithAngle.getValue());
						realmSunPosition.setTimestamp(zenithAngle.getTimestamp());
					}
				});
				Log.i(TAG, "Updated sun position in realm");
			} catch (ProviderException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class UpdateGeomagneticCoordinatesTask extends RealmEnclosedAsyncTask<Void, Void, Void> {
		private final GeomagneticCoordinatesProvider provider;
		private final Location location;

		private UpdateGeomagneticCoordinatesTask(GeomagneticCoordinatesProvider provider, Location location) {
			this.provider = provider;
			this.location = location;
		}

		@Override
		protected Void doInBackgroundWithRealm(Realm realm, Void... params) {
			Log.i(TAG, "Getting degrees from closest pole...");
			final Timestamped<Float> degreesFromClosestPole = provider.getDegreesFromClosestPole(location.getLatitude(), location.getLongitude());
			Log.d(TAG, "Degrees from closest pole is: " + degreesFromClosestPole);

			Log.d(TAG, "Looking up geomagnetic coordinates from realm...");
			final RealmGeomagneticCoordinates realmGeomagneticCoordinates = realm.where(RealmGeomagneticCoordinates.class).findFirst();
			Log.d(TAG, "Realm geomagnetic coordinates are:  " + realmGeomagneticCoordinates);

			Log.d(TAG, "Storing degrees from closest pole in realm");
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realmGeomagneticCoordinates.setDegreesFromClosestPole(degreesFromClosestPole.getValue());
					realmGeomagneticCoordinates.setTimestamp(degreesFromClosestPole.getTimestamp());
				}
			});
			Log.i(TAG, "Updated geomagnetic coordinates in realm");
			return null;
		}
	}
}
