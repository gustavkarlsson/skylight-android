package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import se.gustavkarlsson.aurora_notifier.android.realm.Requirements;

public class ScheduleUpdatesBootReceiver extends BroadcastReceiver {
	private static final String TAG = ScheduleUpdatesBootReceiver.class.getSimpleName();

	private static final String ACTION_SCHEDULE_UPDATES = TAG + ".ACTION_SCHEDULE_UPDATES";

	public static void setupUpdateScheduling(Context context) {
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
		Intent intent = new Intent(ScheduleUpdatesBootReceiver.ACTION_SCHEDULE_UPDATES, null, context, ScheduleUpdatesBootReceiver.class);
		localBroadcastManager.registerReceiver(new ScheduleUpdatesBootReceiver(), new IntentFilter(ScheduleUpdatesBootReceiver.ACTION_SCHEDULE_UPDATES));
		localBroadcastManager.sendBroadcast(intent);
	}

    @Override
    public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive");
		if (intent != null) {
			String action = intent.getAction();
			if (Intent.ACTION_BOOT_COMPLETED.equals(action)
					|| ACTION_SCHEDULE_UPDATES.equals(action)) {
				if (Requirements.isFulfilled()) {
					scheduleUpdates(context);
				} else {
					cancelUpdates(context);
				}
			}
		}
	}

	private static void scheduleUpdates(Context context) {
		Log.v(TAG, "scheduleUpdates");
		GcmNetworkManager manager = GcmNetworkManager.getInstance(context);
		Task task = createTask();
		manager.schedule(task);
		Log.d(TAG, "Schedule set");
	}

	private static void cancelUpdates(Context context) {
		Log.v(TAG, "cancelUpdates");
		GcmNetworkManager manager = GcmNetworkManager.getInstance(context);
		manager.cancelTask(UpdateService.REQUEST_UPDATE, UpdateService.class);
	}

	private static Task createTask() {
		return new PeriodicTask.Builder()
				.setService(UpdateService.class)
				.setPeriod(20 * 60)
				.setFlex(10 * 60)
				.setTag(UpdateService.REQUEST_UPDATE)
				.setPersisted(true)
				.setUpdateCurrent(true)
				.build();
	}


}
