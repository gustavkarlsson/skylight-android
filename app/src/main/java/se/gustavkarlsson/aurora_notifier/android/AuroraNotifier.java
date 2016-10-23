package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import se.gustavkarlsson.aurora_notifier.android.background.BootReceiver;

public class AuroraNotifier extends Application {

	private static final String TAG = AuroraNotifier.class.getSimpleName();

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		setupAlarm();
	}

	private void setupAlarm() {
		Log.v(TAG, "setupAlarm");
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
		Intent intent = new Intent(BootReceiver.ACTION_SETUP_ALARMS, null, this, BootReceiver.class);
		localBroadcastManager.registerReceiver(new BootReceiver(), new IntentFilter(BootReceiver.ACTION_SETUP_ALARMS));
		localBroadcastManager.sendBroadcast(intent);
	}
}
