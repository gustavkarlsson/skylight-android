package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
					trySetAlarms(context);
				} else {
					tryUnsetAlarms(context);
				}
			}
		}
	}

	private static void trySetAlarms(Context context) {
		Log.v(TAG, "trySetAlarms");
		Log.i(TAG, "Trying to set up alarms");
		Intent updateIntent = UpdateService.createUpdateIntent(context);
		if (intentAlreadyCreated(context, updateIntent)) {
			Log.d(TAG, "Alarm was already set up");
			return;
		}
		Log.d(TAG, "Alarm is not yet set up");
		scheduleAlarm(context, updateIntent);
		Log.d(TAG, "Alarm is now set up");
	}

	private static void tryUnsetAlarms(Context context) {
		Log.v(TAG, "tryUnsetAlarms");
		Log.i(TAG, "Trying to unset alarms");
		Intent updateIntent = UpdateService.createUpdateIntent(context);
		if (!intentAlreadyCreated(context, updateIntent)) {
			Log.d(TAG, "Alarm is set up");
			unscheduleAlarm(context, updateIntent);
			Log.d(TAG, "Alarm is no longer set");
			return;
		}
		Log.d(TAG, "Alarm is not yet set up");
	}

	private static boolean intentAlreadyCreated(Context context, Intent intent) {
		return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
	}

	private static void scheduleAlarm(Context context, Intent intent) {
		PendingIntent pendingServiceIntent = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingServiceIntent);
	}

	private static void unscheduleAlarm(Context context, Intent intent) {
		PendingIntent pendingServiceIntent = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingServiceIntent);
	}


}
