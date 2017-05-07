package se.gustavkarlsson.aurora_notifier.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpgradeReceiver extends BroadcastReceiver {
	private static final String TAG = UpgradeReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Package was upgraded");
		RealmSetup.clearCache();
	}
}
