package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerUpdateServiceComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.UpdateServiceComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraDataComplicationsEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.util.Alarm;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;


public class UpdateService extends GcmTaskService implements Updater {
	private static final String TAG = UpdateService.class.getSimpleName();

	public static final String REQUEST_UPDATE = TAG + ".REQUEST_UPDATE";
	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String RESPONSE_UPDATE_FINISHED = TAG + ".RESPONSE_UPDATE_FINISHED";
	public static final String RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION = TAG + ".RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION";

	@Inject
	LocationProvider locationProvider;

	@Inject
	AuroraDataProvider auroraDataProvider;

	private int updateTimeoutMillis;

	private Binder binder;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		UpdateServiceComponent component = DaggerUpdateServiceComponent.builder()
				.googleLocationModule(new GoogleLocationModule(this))
				.weatherModule(new WeatherModule(this.getString(R.string.api_key_openweathermap)))
				.build();
		component.inject(this);
		updateTimeoutMillis = getResources().getInteger(R.integer.update_timeout_millis);
		binder = new UpdaterBinder(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public int onRunTask(TaskParams taskParams) {
		Log.v(TAG, "onRunTask");
		String tag = taskParams.getTag();
		if (REQUEST_UPDATE.equals(tag)) {
			boolean successful = update();
			if (successful) {
				return GcmNetworkManager.RESULT_SUCCESS;
			}
			return GcmNetworkManager.RESULT_RESCHEDULE;
		}
		return GcmNetworkManager.RESULT_FAILURE;
	}

	@Override
	public void onInitializeTasks() {
		Log.v(TAG, "onInitializeTasks");
		ScheduleUpdatesBootReceiver.setupUpdateScheduling(this);
		super.onInitializeTasks();
	}

	@Override
	public boolean update() {
		Log.v(TAG, "update");
		try {
			AuroraEvaluation evaluation = getEvaluation(updateTimeoutMillis);
			broadcastEvaluation(evaluation);
			return true;
		} catch (UserFriendlyException e) {
			String errorMessage = getApplicationContext().getString(e.getStringResourceId());
			Log.e(TAG, "A user friendly exception occurred: " + errorMessage, e);
			broadcastError(errorMessage);
			return false;
		} catch (Exception e) {
			Log.e(TAG, "An unexpected error occurred.", e);
			String errorMessage = getApplicationContext().getString(R.string.error_unknown_update_error);
			broadcastError(errorMessage);
			return false;
		}
	}

	private AuroraEvaluation getEvaluation(long timeoutMillis) {
		Alarm timeoutAlarm = Alarm.start(timeoutMillis);
		Location location = locationProvider.getLocation(timeoutAlarm.getRemainingTimeMillis());

		AuroraData auroraData = auroraDataProvider.getAuroraData(timeoutAlarm.getRemainingTimeMillis(), location);
		List<AuroraComplication> complications = new AuroraDataComplicationsEvaluator(auroraData).evaluate();
		return new AuroraEvaluation(System.currentTimeMillis(), auroraData, complications);
	}

	private void broadcastEvaluation(AuroraEvaluation evaluation) {
		Intent intent = new Intent(RESPONSE_UPDATE_FINISHED);
		Parcelable wrappedEvaluation = Parcels.wrap(evaluation);
		intent.putExtra(RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION, wrappedEvaluation);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void broadcastError(String message) {
		Intent intent = new Intent(RESPONSE_UPDATE_ERROR);
		if (message != null) {
			intent.putExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE, message);
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	public static class UpdaterBinder extends Binder {
		private final Updater updater;

		private UpdaterBinder(Updater updater) {
			this.updater = updater;
		}

		public Updater getUpdater() {
			return updater;
		}
	}
}
