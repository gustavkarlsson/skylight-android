package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.parceler.Parcels;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraReportCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerUpdaterComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.UpdaterComponent;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static se.gustavkarlsson.aurora_notifier.android.AuroraNotifier.getApplicationComponent;

public class Updater {
	private static final String TAG = Updater.class.getSimpleName();

	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String RESPONSE_UPDATE_FINISHED = TAG + ".RESPONSE_UPDATE_FINISHED";
	public static final String RESPONSE_UPDATE_FINISHED_EXTRA_REPORT = TAG + ".RESPONSE_UPDATE_FINISHED_EXTRA_REPORT";

	private final Context context;
	private final AuroraReportCache cache;
	private final LocalBroadcastManager broadcastManager;

	@Inject
	Updater(Context context, AuroraReportCache cache, LocalBroadcastManager broadcastManager) {
		this.context = context;
		this.cache = cache;
		this.broadcastManager = broadcastManager;
	}

	public boolean update(int timeoutMillis) {
		Log.v(TAG, "onUpdate");
		UpdaterComponent component = DaggerUpdaterComponent.builder()
				.applicationComponent(getApplicationComponent(context))
				.build();
		AuroraReportProvider provider = component.getAuroraReportProvider();
		try {
			AuroraReport report = provider.getReport(timeoutMillis);
			cache.setCurrentLocation(report);
			broadcastReport(report);
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

	private void broadcastReport(AuroraReport report) {
		Intent intent = new Intent(RESPONSE_UPDATE_FINISHED);
		Parcelable wrappedReport = Parcels.wrap(report);
		intent.putExtra(RESPONSE_UPDATE_FINISHED_EXTRA_REPORT, wrappedReport);
		broadcastManager.sendBroadcast(intent);
	}

	private void broadcastError(String message) {
		Intent intent = new Intent(RESPONSE_UPDATE_ERROR);
		if (message != null) {
			intent.putExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE, message);
		}
		broadcastManager.sendBroadcast(intent);
	}
}
