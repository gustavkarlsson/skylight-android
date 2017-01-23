package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticCoordinatesProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.KpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GeomagneticCoordinatesProviderImpl;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.KlausBrunnerSunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedKpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateGeomagneticCoordinatesTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateKpIndexTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateSunPositionTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateWeatherTask;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmDebug;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

public class PollingService extends WakefulIntentService {

	private static final String TAG = PollingService.class.getSimpleName();

	public static final String ACTION_UPDATE_FINISHED = TAG + ".UPDATE_FINISHED";
	public static final String ACTION_UPDATE_FINISHED_MESSAGE = TAG + ".UPDATE_FINISHED_MESSAGE";

	private static final String ACTION_UPDATE = TAG + ".UPDATE";
	private static final long UPDATE_TIMEOUT_MILLIS = 60_000;

	private KpIndexProvider kpIndexProvider;
	private WeatherProvider weatherProvider;
	private SunPositionProvider sunPositionProvider;
	private GeomagneticCoordinatesProvider geomagneticCoordinatesProvider;
	private GoogleApiClient googleApiClient;
	private LocalBroadcastManager broadcastManager;

	// Default constructor required
	public PollingService() {
		super(PollingService.class.getSimpleName());
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
		broadcastManager = LocalBroadcastManager.getInstance(this);
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
		try (Realm realm = Realm.getDefaultInstance()) {
			if (BuildConfig.DEBUG) {
				final RealmDebug realmDebug = realm.where(RealmDebug.class).findFirst();
				if (updateDebug(realm, realmDebug)) {
					return;
				}
			}
			updateReal();
		}
	}

	// TODO move this to a provider
	private boolean updateDebug(Realm realm, final RealmDebug realmDebug) {
		if (realmDebug.isEnabled()) {
			final RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
			final RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
			final RealmSunPosition realmSunPosition = realm.where(RealmSunPosition.class).findFirst();
			final RealmGeomagneticCoordinates realmGeomagneticCoordinates = realm.where(RealmGeomagneticCoordinates.class).findFirst();
			final long timestamp = System.currentTimeMillis();
			realm.executeTransaction(r -> {
                realmKpIndex.setKpIndex(realmDebug.getKpIndex());
                realmWeather.setCloudPercentage(realmDebug.getCloudPercentage());
                realmSunPosition.setZenithAngle(realmDebug.getZenithAngle());
                realmGeomagneticCoordinates.setDegreesFromClosestPole(realmDebug.getDegreesFromClosestPole());
                realmKpIndex.setTimestamp(timestamp);
                realmWeather.setTimestamp(timestamp);
                realmSunPosition.setTimestamp(timestamp);
                realmGeomagneticCoordinates.setTimestamp(timestamp);
            });
			notifyListeners("Updated with debug values");
			return true;
		}
		return false;
	}

	private void updateReal() {
		try {
			Location location = getLocation(googleApiClient);
			if (location == null) {
				Log.w(TAG, "Could not get location. Stopping update");
				return;
			}
			Log.d(TAG, "Location is: " + location);
			ParallelTaskRunner.TaskExecutionReport report = runUpdateTasks(UPDATE_TIMEOUT_MILLIS, location);
			if (report.hasErrors()) {
				logTaskExecutionErrors(report);
				notifyListeners("Updated finished with errors");
			}
			notifyListeners(null);
		} catch (SecurityException e) {
			Log.e(TAG, "Location permission not given", e);
		} finally {
			if (googleApiClient.isConnected()) {
				Log.d(TAG, "Disconnecting from Google Play Services");
				googleApiClient.disconnect();
			}
		}
	}

	private void notifyListeners(String message) {
		Intent intent = new Intent(ACTION_UPDATE_FINISHED);
		if (message != null) {
			intent.putExtra(ACTION_UPDATE_FINISHED_MESSAGE, message);
		}
		broadcastManager.sendBroadcast(intent);
	}

	private static Location getLocation(GoogleApiClient googleApiClient) {
		Log.i(TAG, "Connecting to Google Play Services...");
		ConnectionResult connectionResult = googleApiClient.blockingConnect(UPDATE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		if (!connectionResult.isSuccess()) {
			Log.w(TAG, "Failed to connect to Google Play Services" +
					". Error code: " + connectionResult.getErrorCode() +
					". Error message: " + connectionResult.getErrorMessage());
			return null;
		}
		Log.d(TAG, "Successfully connected to Google Play Services");
		try {
			Log.d(TAG, "Getting location");
			return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
		} catch (SecurityException e) {
			Log.w(TAG, "Location permission missing", e);
			return null;
		}
	}

	private ParallelTaskRunner.TaskExecutionReport runUpdateTasks(long timeoutMillis, Location location) {
		AsyncTask updateKpIndexTask = new UpdateKpIndexTask(kpIndexProvider);
		AsyncTask updateWeatherTask = new UpdateWeatherTask(weatherProvider, location);
		AsyncTask updateSunPositionTask = new UpdateSunPositionTask(sunPositionProvider, location, System.currentTimeMillis());
		AsyncTask updateGeomagneticCoordinatesTask = new UpdateGeomagneticCoordinatesTask(geomagneticCoordinatesProvider, location);

		return new ParallelTaskRunner().executeInParallel(timeoutMillis,
				updateKpIndexTask,
				updateWeatherTask,
				updateSunPositionTask,
				updateGeomagneticCoordinatesTask);
	}

	private static void logTaskExecutionErrors(ParallelTaskRunner.TaskExecutionReport report) {
		StringBuilder sb = new StringBuilder("The following errors occurred during update:\n");
		if (!report.getTimeoutExceptions().isEmpty()) {
			sb.append("Timeouts:\n");
			for (AsyncTask task : report.getTimeoutExceptions().keySet()) {
				sb.append("    ").append(task.toString()).append("\n");
			}
		}
		if (!report.getExecutionExceptions().isEmpty()) {
			sb.append("Timeouts:\n");
			for (ExecutionException exception : report.getExecutionExceptions().values()) {
				sb.append("    ").append(exception.toString()).append("\n");
			}
		}
		Log.w(TAG, sb.toString());
	}

	public static Intent createUpdateIntent(Context context) {
		return new Intent(ACTION_UPDATE, null, context, PollingService.class);
	}

	public static void requestUpdate(Context context) {
		Intent updateIntent = createUpdateIntent(context);
		WakefulIntentService.sendWakefulWork(context, updateIntent);
	}

}
