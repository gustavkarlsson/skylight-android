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

	private static Context mContext;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		mContext = this;
		setupAlarm();
	}

	public static Context getContext(){
		return mContext;
	}

	private void setupAlarm() {
		Log.v(TAG, "setupAlarm");
		Intent intent = new Intent(Intent.ACTION_RUN, null, this, BootReceiver.class);
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(new BootReceiver(), new IntentFilter(Intent.ACTION_RUN));
		localBroadcastManager.sendBroadcast(intent);
	}
}
