package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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

public class AuroraPollingService extends WakefulIntentService {

	private static final String TAG = AuroraPollingService.class.getSimpleName();
	private static final String ACTION_UPDATE = TAG + ".UPDATE";

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
		Realm realm = Realm.getDefaultInstance();
		try {
			updateKpIndex(realm);

			Log.d(TAG, "Connecting to Google Play Servides...");
			ConnectionResult connectionResult = googleApiClient.blockingConnect();
			if (connectionResult.isSuccess()) {
				Log.d(TAG, "Successfully connected to Google Play Services");
				Log.d(TAG, "Getting location");
				Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
				if (location != null) {
					Log.d(TAG, "Location is: " + location);
					updateWeather(realm, location);
					updateSunPosition(realm, location, System.currentTimeMillis());
					updateGeomagneticCoordinates(realm, location);
				} else {
					Log.w(TAG, "Could not get location");
				}
			} else {
				Log.e(TAG, "Failed to connect to Google Play Services: " + connectionResult.getErrorMessage());
			}
		} catch (SecurityException e) {
			Log.e(TAG, "Location permission not given");
			// TODO Handle errors better
			e.printStackTrace();
		} finally {
			if (!realm.isClosed()) {
				realm.close();
			}
			if (googleApiClient.isConnected()) {
				Log.d(TAG, "Disconnecting from Google Play Services");
				googleApiClient.disconnect();
			}
		}
	}

	private void updateKpIndex(Realm realm) {
		try {
			Log.d(TAG, "Getting KP index...");
			final Timestamped<Float> kpIndex = kpIndexProvider.getKpIndex();
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
			Log.d(TAG, "Stored KP index in realm");
		} catch (ProviderException e) {
			// TODO Handle errors better
			e.printStackTrace();
		}
	}

	private void updateWeather(Realm realm, Location location) {
		try {
			Log.d(TAG, "Getting weather...");
			final Timestamped<? extends Weather> weather = weatherProvider.getWeather(location.getLatitude(), location.getLongitude());
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
			Log.d(TAG, "Stored weather in realm");
		} catch (ProviderException e) {
			// TODO Handle errors better
			e.printStackTrace();
		}
	}

	private void updateSunPosition(Realm realm, Location location, long timeMillis) {
		try {
			Log.d(TAG, "Getting sun position...");
			final Timestamped<Float> zenithAngle = sunPositionProvider.getZenithAngle(timeMillis, location.getLatitude(), location.getLongitude());
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
			Log.d(TAG, "Stored sun position in realm");
		} catch (ProviderException e) {
			// TODO Handle errors better
			e.printStackTrace();
		}
	}

	private void updateGeomagneticCoordinates(Realm realm, Location location) {
		Log.d(TAG, "Getting degrees from closest pole...");
		final Timestamped<Float> degreesFromClosestPole = geomagneticCoordinatesProvider.getDegreesFromClosestPole(location.getLatitude(), location.getLongitude());
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
		Log.d(TAG, "Stored geomagnetic coordinates in realm");
	}

	public static Intent createUpdateIntent(Context context) {
		return new Intent(ACTION_UPDATE, null, context, AuroraPollingService.class);
	}

	public static void sendUpdateRequest(Context context) {
		Intent updateIntent = createUpdateIntent(context);
		WakefulIntentService.sendWakefulWork(context, updateIntent);
	}
}
