package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive");
		if (intent != null) {
			String action = intent.getAction();
			if (Intent.ACTION_BOOT_COMPLETED.equals(action)
					|| Intent.ACTION_RUN.equals(action)) {
				setupAlarms(context);
			}
		}
    }

	private void setupAlarms(Context context) {
		Log.v(TAG, "setupAlarms");
		Log.i(TAG, "Setting up alarms");
		Intent updateIntent = new Intent(KpIndexService.ACTION_UPDATE, null, context, KpIndexService.class);
		if (!intentAlreadyCreated(context, updateIntent)) {
			Log.d(TAG, "Alarm is not yet set up");
			PendingIntent pendingServiceIntent = PendingIntent.getService(context, 0, updateIntent, 0);
			scheduleAlarm(context, pendingServiceIntent);
			Log.d(TAG, "Alarm is now set up");
		} else {
			Log.d(TAG, "Alarm was already set up");
		}
	}

	private void scheduleAlarm(Context context, PendingIntent pendingServiceIntent) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingServiceIntent);
	}

	private boolean intentAlreadyCreated(Context context, Intent intent) {
		return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
	}


}
