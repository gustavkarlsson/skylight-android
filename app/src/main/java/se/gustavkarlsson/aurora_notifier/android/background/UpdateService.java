package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
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
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.LocalBroadcastManagerModule;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraDataComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.util.Alarm;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;


public class UpdateService extends GcmTaskService {
	private static final String TAG = UpdateService.class.getSimpleName();

	public static final String REQUEST_UPDATE = TAG + ".REQUEST_UPDATE";
	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String RESPONSE_UPDATE_FINISHED = TAG + ".RESPONSE_UPDATE_FINISHED";
	public static final String RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION = TAG + ".RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION";

	private static final long UPDATE_TIMEOUT_MILLIS = R.integer.update_timeout_millis;

	private Handler handler;

	@Inject
	LocalBroadcastManager broadcastManager;

	@Inject
	LocationProvider locationProvider;

	@Inject
	AuroraDataProvider auroraDataProvider;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		handler = createHandler();
		UpdateServiceComponent component = DaggerUpdateServiceComponent.builder()
				.localBroadcastManagerModule(new LocalBroadcastManagerModule(this))
				.googleLocationModule(new GoogleLocationModule(this))
				.build();
		component.inject(this);
	}

	private static Handler createHandler() {
		HandlerThread thread = new HandlerThread(TAG + ".updater", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		Looper looper = thread.getLooper();
		return new Handler(looper);
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
	public int onStartCommand(Intent intent, int i, int i1) {
		handler.post(this::update);
		return super.onStartCommand(intent, i, i1);
	}

	@Override
	public void onInitializeTasks() {
		Log.v(TAG, "onInitializeTasks");
		ScheduleUpdatesBootReceiver.setupUpdateScheduling(this);
		super.onInitializeTasks();
	}

	private boolean update() {
		Log.v(TAG, "update");
		try {
			AuroraEvaluation evaluation = getEvaluation(UPDATE_TIMEOUT_MILLIS);
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
		List<AuroraComplication> complications = new AuroraDataComplicationEvaluator(auroraData).evaluate();
		return new AuroraEvaluation(System.currentTimeMillis(), auroraData, complications);
	}

	private void broadcastEvaluation(AuroraEvaluation evaluation) {
		Intent intent = new Intent(RESPONSE_UPDATE_FINISHED);
		Parcelable wrappedEvaluation = Parcels.wrap(evaluation);
		intent.putExtra(RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION, wrappedEvaluation);
		broadcastManager.sendBroadcast(intent);
	}

	private void broadcastError(String message) {
		Intent intent = new Intent(RESPONSE_UPDATE_ERROR);
		if (message != null) {
			intent.putExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE, message);
		}
		broadcastManager.sendBroadcast(intent);
	}

	public static void start(Context context) {
		Intent intent = new Intent(context, UpdateService.class);
		context.startService(intent);
	}
}
