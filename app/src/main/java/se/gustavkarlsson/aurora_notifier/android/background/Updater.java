package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.parceler.Parcels;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraEvaluationCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerUpdaterComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.UpdaterComponent;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static se.gustavkarlsson.aurora_notifier.android.AuroraNotifier.getApplicationComponent;

public class Updater {
	private static final String TAG = Updater.class.getSimpleName();

	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String RESPONSE_UPDATE_FINISHED = TAG + ".RESPONSE_UPDATE_FINISHED";
	public static final String RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION = TAG + ".RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION";

	private final Context context;
	private final AuroraEvaluationCache cache;

	public Updater(Context context, AuroraEvaluationCache cache) {
		this.context = context;
		this.cache = cache;
	}

	public boolean update(int timeoutMillis) {
		Log.v(TAG, "onUpdate");
		UpdaterComponent component = DaggerUpdaterComponent.builder()
				.applicationComponent(getApplicationComponent(context))
				.build();
		AuroraEvaluationProvider provider = component.getAuroraEvaluationProvider();
		try {
			AuroraEvaluation evaluation = provider.getEvaluation(timeoutMillis);
			cache.setCurrentLocation(evaluation);
			broadcastEvaluation(evaluation);
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
