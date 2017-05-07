package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.parceler.Parcels;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerUpdateJobComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.realm.EvaluationCache;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

public class Updater {
	private static final String TAG = Updater.class.getSimpleName();

	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String RESPONSE_UPDATE_FINISHED = TAG + ".RESPONSE_UPDATE_FINISHED";
	public static final String RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION = TAG + ".RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION";

	@Inject
	AuroraEvaluationProvider evaluationProvider;

	private final Context context;
	private final int updateTimeoutMillis;

	public Updater(Context context, int updateTimeoutMillis) {
		this.context = context;
		this.updateTimeoutMillis = updateTimeoutMillis;
		DaggerUpdateJobComponent.builder()
				.googleLocationModule(new GoogleLocationModule(context))
				.weatherModule(new WeatherModule(context.getString(R.string.api_key_openweathermap)))
				.build()
				.inject(this);
	}

	public boolean update() {
		Log.v(TAG, "onUpdate");
		try {
			AuroraEvaluation evaluation = evaluationProvider.getEvaluation(updateTimeoutMillis);
			broadcastEvaluation(evaluation);
			EvaluationCache.set(evaluation);
			return true;
		} catch (UserFriendlyException e) {
			String errorMessage = context.getString(e.getStringResourceId());
			Log.e(TAG, "A user friendly exception occurred: " + errorMessage, e);
			broadcastError(errorMessage);
			return false;
		} catch (Exception e) {
			Log.e(TAG, "An unexpected error occurred.", e);
			String errorMessage = context.getString(R.string.error_unknown_update_error);
			broadcastError(errorMessage);
			return false;
		}
	}

	private void broadcastEvaluation(AuroraEvaluation evaluation) {
		Intent intent = new Intent(RESPONSE_UPDATE_FINISHED);
		Parcelable wrappedEvaluation = Parcels.wrap(evaluation);
		intent.putExtra(RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION, wrappedEvaluation);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	private void broadcastError(String message) {
		Intent intent = new Intent(RESPONSE_UPDATE_ERROR);
		if (message != null) {
			intent.putExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE, message);
		}
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
