package se.gustavkarlsson.skylight.android.background;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.cache.LastReportCache;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.notifications.NotificationHandler;
import se.gustavkarlsson.skylight.android.observers.ObservableData;
import se.gustavkarlsson.skylight.android.util.UserFriendlyException;

import static se.gustavkarlsson.skylight.android.Skylight.getApplicationComponent;
import static se.gustavkarlsson.skylight.android.dagger.modules.definitive.LatestAuroraReportObservableModule.LATEST_NAME;

@Reusable
public class Updater {
	private static final String TAG = Updater.class.getSimpleName();

	public static final String RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR";
	public static final String RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE";

	private final Context context;
	private final LastReportCache cache;
	private final LocalBroadcastManager broadcastManager;
	private final NotificationHandler notificationHandler;
	private final ObservableData<AuroraReport> latestAuroraReport;

	@Inject
	Updater(Context context, LastReportCache cache, LocalBroadcastManager broadcastManager, NotificationHandler notificationHandler, @Named(LATEST_NAME) ObservableData<AuroraReport> latestAuroraReport) {
		this.context = context;
		this.cache = cache;
		this.broadcastManager = broadcastManager;
		this.notificationHandler = notificationHandler;
		this.latestAuroraReport = latestAuroraReport;
	}

	public boolean update(long timeoutMillis) {
		Log.v(TAG, "onUpdate");
		AuroraReportProvider provider = getApplicationComponent().getAuroraReportProvider();
		AuroraReport report;
		try {
			report = provider.getReport(timeoutMillis);
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
		cache.set(report);
		latestAuroraReport.setData(report);
		notificationHandler.handle(report);
		return true;
	}

	private void broadcastError(String message) {
		Intent intent = new Intent(RESPONSE_UPDATE_ERROR);
		if (message != null) {
			intent.putExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE, message);
		}
		broadcastManager.sendBroadcast(intent);
	}
}
