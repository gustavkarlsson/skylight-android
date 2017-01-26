package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import java8.util.J8Arrays;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GeomagneticLocationProviderImpl;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.KlausBrunnerSunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedSolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateGeomagneticLocationTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateSolarActivityTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateSunPositionTask;
import se.gustavkarlsson.aurora_notifier.android.background.update_tasks.UpdateWeatherTask;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraDataComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;
import se.gustavkarlsson.aurora_notifier.android.util.PermissionUtils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.error;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.value;

public class PollingService extends WakefulIntentService {

	private static final String TAG = PollingService.class.getSimpleName();

	public static final String ACTION_UPDATE_ERROR = TAG + ".ACTION_UPDATE_ERROR";
	public static final String ACTION_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".ACTION_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String ACTION_UPDATE_FINISHED = TAG + ".ACTION_UPDATE_FINISHED";
	public static final String ACTION_UPDATE_FINISHED_EXTRA_EVALUATION = TAG + ".ACTION_UPDATE_FINISHED_EXTRA_EVALUATION";

	private static final String ACTION_REQUEST_UPDATE = TAG + ".ACTION_REQUEST_UPDATE";
	private static final long UPDATE_TIMEOUT_MILLIS = 60_000;

	private SolarActivityProvider solarActivityProvider;
	private WeatherProvider weatherProvider;
	private SunPositionProvider sunPositionProvider;
	private GeomagneticLocationProvider geomagneticLocationProvider;
	private GoogleApiClient googleApiClient;
	private LocalBroadcastManager broadcastManager;

	private final AtomicBoolean updating = new AtomicBoolean(false);

	// Default constructor required
	public PollingService() {
		super(PollingService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		if (solarActivityProvider == null) {
			solarActivityProvider = RetrofittedSolarActivityProvider.createDefault();
		}
		if (weatherProvider == null) {
			weatherProvider = RetrofittedOpenWeatherMapProvider.createDefault();
		}
		if (sunPositionProvider == null) {
			sunPositionProvider = new KlausBrunnerSunPositionProvider();
		}
		if (geomagneticLocationProvider == null) {
			geomagneticLocationProvider = new GeomagneticLocationProviderImpl();
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
			if (ACTION_REQUEST_UPDATE.equals(intent.getAction())) {
				if (PermissionUtils.hasLocationPermission(this) && !updating.getAndSet(true)) {
					update();
					updating.set(false);
				}
			}
		}
	}

	private void update() {
		Log.v(TAG, "update");
		Alarm timeoutAlarm = Alarm.start(UPDATE_TIMEOUT_MILLIS);
		try {
			ValueOrError<AuroraEvaluation> possibleEvaluation = getEvaluation(timeoutAlarm);
			if (!possibleEvaluation.isValue()) {
				String errorMessage = getApplicationContext().getString(possibleEvaluation.getErrorStringResource());
				broadcastError(errorMessage);
			} else {
				broadcastEvaluation(possibleEvaluation.getValue());
			}
		} catch (Exception e) {
			String errorMessage = getApplicationContext().getString(R.string.unknown_update_error);
			broadcastError(errorMessage);
		} finally {
			if (googleApiClient.isConnected()) {
				Log.d(TAG, "Disconnecting from Google Play Services");
				googleApiClient.disconnect();
			}
		}
	}

	private ValueOrError<AuroraEvaluation> getEvaluation(Alarm timeoutAlarm) {
		ValueOrError<Location> locationOrError = getLocation(timeoutAlarm);
		if (!locationOrError.isValue()) {
			return error(locationOrError.getErrorStringResource());
		}

		ValueOrError<AuroraData> auroraDataOrError = getAuroraData(timeoutAlarm, locationOrError.getValue());
		if (!auroraDataOrError.isValue()) {
			return error(auroraDataOrError.getErrorStringResource());
		}
		AuroraData data = auroraDataOrError.getValue();
		List<AuroraComplication> complications = new AuroraDataComplicationEvaluator(data).evaluate();
		AuroraEvaluation evaluation = new AuroraEvaluation(System.currentTimeMillis(), data, complications);
		return value(evaluation);
	}

	private ValueOrError<Location> getLocation(Alarm timeoutAlarm) {
		Log.i(TAG, "Connecting to Google Play Services...");
		try {
			ConnectionResult connectionResult = googleApiClient.blockingConnect(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			if (!connectionResult.isSuccess()) {
				Log.w(TAG, "Could not connect to Google Play Services" +
						". Error code: " + connectionResult.getErrorCode() +
						". Error message: " + connectionResult.getErrorMessage());
				if (connectionResult.getErrorCode() == ConnectionResult.TIMEOUT) {
					return error(R.string.update_took_too_long);
				}
				return error(R.string.could_not_connect_to_google_play_services);
			}
			Log.d(TAG, "Successfully connected to Google Play Services");
			Log.d(TAG, "Getting location");
			Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
			Log.d(TAG, "GeomagneticLocation is: " + location);
			if (location == null) {
				error(R.string.could_not_determine_location);
			}
			return value(location);
		} catch (SecurityException e) {
			Log.w(TAG, "GeomagneticLocation permission missing", e);
			return error(R.string.location_permission_missing);
		}
	}

	private ValueOrError<AuroraData> getAuroraData(Alarm timeoutAlarm, Location location) {
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
			ValueOrError<SolarActivity> solarActivityOrError = updateSolarActivityTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			if (!solarActivityOrError.isValue()) {
				return error(solarActivityOrError.getErrorStringResource());
			}
			ValueOrError<Weather> weatherOrError = updateWeatherTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			if (!weatherOrError.isValue()) {
				return error(weatherOrError.getErrorStringResource());
			}
			ValueOrError<SunPosition> sunPositionOrError = updateSunPositionTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			if (!sunPositionOrError.isValue()) {
				return error(sunPositionOrError.getErrorStringResource());
			}
			ValueOrError<GeomagneticLocation> geomagneticLocationOrError = updateGeomagneticLocationTask.get(timeoutAlarm.getRemainingTimeMillis(), MILLISECONDS);
			if (!geomagneticLocationOrError.isValue()) {
				return error(geomagneticLocationOrError.getErrorStringResource());
			}

			AuroraData data = new AuroraData(
					solarActivityOrError.getValue(),
					geomagneticLocationOrError.getValue(),
					sunPositionOrError.getValue(),
					weatherOrError.getValue()
			);
			return value(data);
		} catch (InterruptedException e) {
			return error(R.string.unknown_update_error);
		} catch (ExecutionException e) {
			return error(R.string.unknown_update_error);
		} catch (TimeoutException e) {
			return error(R.string.update_took_too_long);
		}
	}

	private void executeInParallel(AsyncTask... tasks) {
		Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
		J8Arrays.stream(tasks)
				.forEach(task -> task.executeOnExecutor(executor));
	}

	private void broadcastEvaluation(AuroraEvaluation evaluation) {
		Intent intent = new Intent(ACTION_UPDATE_FINISHED);
		Parcelable wrappedEvaluation = Parcels.wrap(evaluation);
		intent.putExtra(ACTION_UPDATE_FINISHED_EXTRA_EVALUATION, wrappedEvaluation);
		broadcastManager.sendBroadcast(intent);
	}

	private void broadcastError(String message) {
		Intent intent = new Intent(ACTION_UPDATE_ERROR);
		if (message != null) {
			intent.putExtra(ACTION_UPDATE_ERROR_EXTRA_MESSAGE, message);
		}
		broadcastManager.sendBroadcast(intent);
	}

	public static Intent createUpdateIntent(Context context) {
		return new Intent(ACTION_REQUEST_UPDATE, null, context, PollingService.class);
	}

	public static void requestUpdate(Context context) {
		Intent updateIntent = createUpdateIntent(context);
		WakefulIntentService.sendWakefulWork(context, updateIntent);
	}

}
