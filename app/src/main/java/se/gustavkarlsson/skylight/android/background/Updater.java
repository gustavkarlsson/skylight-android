package se.gustavkarlsson.skylight.android.background;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.parceler.Parcels;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.cache.AuroraReportCache;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.notifications.NotificationHandler;
import se.gustavkarlsson.skylight.android.util.UserFriendlyException;

import static se.gustavkarlsson.skylight.android.Skylight.getApplicationComponent;

public class Updater {
	private static final String TAG = Updater.class.getSimpleName();

	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";
	public static final String RESPONSE_UPDATE_FINISHED = TAG + ".RESPONSE_UPDATE_FINISHED";
	public static final String RESPONSE_UPDATE_FINISHED_EXTRA_REPORT = TAG + ".RESPONSE_UPDATE_FINISHED_EXTRA_REPORT";

	private final Context context;
	private final AuroraReportCache cache;
	private final LocalBroadcastManager broadcastManager;
	private final NotificationHandler notificationHandler;

	@Inject
	Updater(Context context, AuroraReportCache cache, LocalBroadcastManager broadcastManager, NotificationHandler notificationHandler) {
		this.context = context;
		this.cache = cache;
		this.broadcastManager = broadcastManager;
		this.notificationHandler = notificationHandler;
	}

	public boolean update(int timeoutMillis) {
		Log.v(TAG, "onUpdate");
		AuroraReportProvider provider = getApplicationComponent().getAuroraReportProvider();
		try {
			AuroraReport report = provider.getReport(timeoutMillis);
			cache.set(report);
			broadcastReport(report);
			notificationHandler.handle(report);
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
