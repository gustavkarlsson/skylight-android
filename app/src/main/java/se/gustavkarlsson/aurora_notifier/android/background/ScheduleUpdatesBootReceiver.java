package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ScheduleUpdatesBootReceiver extends BroadcastReceiver {
	private static final String TAG = ScheduleUpdatesBootReceiver.class.getSimpleName();

	public static final String ACTION_SETUP_ALARMS = TAG + ".ACTION_SETUP_ALARMS";

	public static void requestScheduleUpdates(Context context) {
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
		Intent intent = new Intent(ScheduleUpdatesBootReceiver.ACTION_SETUP_ALARMS, null, context, ScheduleUpdatesBootReceiver.class);
		localBroadcastManager.registerReceiver(new ScheduleUpdatesBootReceiver(), new IntentFilter(ScheduleUpdatesBootReceiver.ACTION_SETUP_ALARMS));
		localBroadcastManager.sendBroadcast(intent);
	}

    @Override
    public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive");
		if (intent != null) {
			String action = intent.getAction();
			if (Intent.ACTION_BOOT_COMPLETED.equals(action)
					|| ACTION_SETUP_ALARMS.equals(action)) {
				setupAlarms(context);
			}
		}
	}

	private static void setupAlarms(Context context) {
		Log.v(TAG, "setupAlarms");
		Log.i(TAG, "Setting up alarms");
		Intent updateIntent = UpdateService.createUpdateIntent(context);
		if (!intentAlreadyCreated(context, updateIntent)) {
			Log.d(TAG, "Alarm is not yet set up");
			scheduleAlarm(context, updateIntent);
			Log.d(TAG, "Alarm is now set up");
		} else {
			Log.d(TAG, "Alarm was already set up");
		}
	}

	private static void scheduleAlarm(Context context, Intent intent) {
		PendingIntent pendingServiceIntent = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingServiceIntent);
	}

	private static boolean intentAlreadyCreated(Context context, Intent intent) {
		return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
	}


}
