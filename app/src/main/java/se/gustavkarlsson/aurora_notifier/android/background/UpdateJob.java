package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.evernote.android.job.Job;

import org.parceler.Parcels;

import java.io.IOException;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.caching.PersistentCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerUpdateServiceComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.PersistentCacheModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;


public class UpdateJob extends Job {
	private static final String TAG = UpdateJob.class.getSimpleName();

	public static final String CACHE_KEY_EVALUATION = "CACHE_KEY_EVALUATION";
	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String RESPONSE_UPDATE_FINISHED = TAG + ".RESPONSE_UPDATE_FINISHED";
	public static final String RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION = TAG + ".RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION";

	@Inject
	AuroraEvaluationProvider evaluationProvider;

	@Inject
	PersistentCache<Parcelable> persistentCache;

	private int updateTimeoutMillis;

	@NonNull
	@Override
	protected Result onRunJob(Params params) {
		setup();
		boolean successful = update();
		try {
			persistentCache.close();
		} catch (IOException e) {
			Log.e(TAG, "Failed to close cache", e);
		}
		return successful ? Result.SUCCESS : Result.FAILURE;
	}

	private void setup() {
		Log.v(TAG, "setup");
		DaggerUpdateServiceComponent.builder()
				.googleLocationModule(new GoogleLocationModule(getContext()))
				.weatherModule(new WeatherModule(getContext().getString(R.string.api_key_openweathermap)))
				.persistentCacheModule(new PersistentCacheModule(getContext()))
				.build()
				.inject(this);
		updateTimeoutMillis = getContext().getResources().getInteger(R.integer.update_timeout_millis);
	}

	private boolean update() {
		Log.v(TAG, "onUpdate");
		try {
			AuroraEvaluation evaluation = evaluationProvider.getEvaluation(updateTimeoutMillis);
			broadcastEvaluation(evaluation);
			saveToCache(evaluation);
			return true;
		} catch (UserFriendlyException e) {
			String errorMessage = getContext().getString(e.getStringResourceId());
			Log.e(TAG, "A user friendly exception occurred: " + errorMessage, e);
			broadcastError(errorMessage);
			return false;
		} catch (Exception e) {
			Log.e(TAG, "An unexpected error occurred.", e);
			String errorMessage = getContext().getString(R.string.error_unknown_update_error);
			broadcastError(errorMessage);
			return false;
		}
	}

	private void broadcastEvaluation(AuroraEvaluation evaluation) {
		Intent intent = new Intent(RESPONSE_UPDATE_FINISHED);
		Parcelable wrappedEvaluation = Parcels.wrap(evaluation);
		intent.putExtra(RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION, wrappedEvaluation);
		LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
	}

	private void broadcastError(String message) {
		Intent intent = new Intent(RESPONSE_UPDATE_ERROR);
		if (message != null) {
			intent.putExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE, message);
		}
		LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
	}

	private void saveToCache(AuroraEvaluation evaluation) {
		Parcelable parcel = Parcels.wrap(evaluation);
		persistentCache.set(CACHE_KEY_EVALUATION, parcel);
	}
}
