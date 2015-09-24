package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class AuroraNotifier extends Application {

	private static final String TAG = "AuroraNotifier";

	private static Context mContext;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		mContext = this;
	}

	public static Context getContext(){
		return mContext;
	}
}
