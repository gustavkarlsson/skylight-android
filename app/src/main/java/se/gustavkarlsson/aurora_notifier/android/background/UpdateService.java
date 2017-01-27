package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerUpdateServiceComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.UpdateServiceComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.UpdateServiceModule;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraDataComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.util.Alarm;
import se.gustavkarlsson.aurora_notifier.android.util.PermissionUtils;

import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.error;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.value;

public class UpdateService extends WakefulIntentService {
	private static final String TAG = UpdateService.class.getSimpleName();

	public static final String ACTION_UPDATE_ERROR = TAG + ".ACTION_UPDATE_ERROR";
	public static final String ACTION_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".ACTION_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String ACTION_UPDATE_FINISHED = TAG + ".ACTION_UPDATE_FINISHED";
	public static final String ACTION_UPDATE_FINISHED_EXTRA_EVALUATION = TAG + ".ACTION_UPDATE_FINISHED_EXTRA_EVALUATION";

	private static final String ACTION_REQUEST_UPDATE = TAG + ".ACTION_REQUEST_UPDATE";

	private final AtomicBoolean updating = new AtomicBoolean(false);

	@Inject @Named(UpdateServiceModule.NAME_UPDATE_TIMEOUT_MILLIS)
	long updateTimeoutMillis;

	@Inject
	LocalBroadcastManager broadcastManager;

	@Inject
	LocationProvider locationProvider;

	@Inject
	AuroraDataProvider auroraDataProvider;

	// Default constructor required
	public UpdateService() {
		super(UpdateService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		UpdateServiceComponent component = DaggerUpdateServiceComponent.builder()
				.updateServiceModule(new UpdateServiceModule(this))
				.build();
		component.inject(this);
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
		try {
			ValueOrError<AuroraEvaluation> possibleEvaluation = getEvaluation(updateTimeoutMillis);
			if (!possibleEvaluation.isValue()) {
				String errorMessage = getApplicationContext().getString(possibleEvaluation.getErrorStringResource());
				broadcastError(errorMessage);
			} else {
				broadcastEvaluation(possibleEvaluation.getValue());
			}
		} catch (Exception e) {
			String errorMessage = getApplicationContext().getString(R.string.unknown_update_error);
			broadcastError(errorMessage);
		}
	}

	private ValueOrError<AuroraEvaluation> getEvaluation(long timeoutMillis) {
		Alarm timeoutAlarm = Alarm.start(timeoutMillis);
		ValueOrError<Location> locationOrError = locationProvider.getLocation(timeoutAlarm.getRemainingTimeMillis());
		if (!locationOrError.isValue()) {
			return error(locationOrError.getErrorStringResource());
		}

		ValueOrError<AuroraData> auroraDataOrError = auroraDataProvider.getAuroraData(timeoutAlarm.getRemainingTimeMillis(), locationOrError.getValue());
		if (!auroraDataOrError.isValue()) {
			return error(auroraDataOrError.getErrorStringResource());
		}
		AuroraData data = auroraDataOrError.getValue();
		List<AuroraComplication> complications = new AuroraDataComplicationEvaluator(data).evaluate();
		AuroraEvaluation evaluation = new AuroraEvaluation(System.currentTimeMillis(), data, complications);
		return value(evaluation);
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
		return new Intent(ACTION_REQUEST_UPDATE, null, context, UpdateService.class);
	}

	public static void requestUpdate(Context context) {
		Intent updateIntent = createUpdateIntent(context);
		WakefulIntentService.sendWakefulWork(context, updateIntent);
	}

}
