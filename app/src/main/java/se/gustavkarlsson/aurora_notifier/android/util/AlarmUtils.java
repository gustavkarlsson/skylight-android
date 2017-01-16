package se.gustavkarlsson.aurora_notifier.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.AuroraNotifier;
import se.gustavkarlsson.aurora_notifier.android.background.BootReceiver;

public class AlarmUtils {

	private AlarmUtils() {
	}

	private static final String TAG = AlarmUtils.class.getSimpleName();

	public static void setupAlarms(Context context) {
		Log.v(TAG, "setupAlarms");
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
		Intent intent = new Intent(BootReceiver.ACTION_SETUP_ALARMS, null, context, BootReceiver.class);
		localBroadcastManager.registerReceiver(new BootReceiver(), new IntentFilter(BootReceiver.ACTION_SETUP_ALARMS));
		localBroadcastManager.sendBroadcast(intent);
	}
}
