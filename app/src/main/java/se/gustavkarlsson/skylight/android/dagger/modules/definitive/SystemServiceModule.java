package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public abstract class SystemServiceModule {

	@Provides
	@Reusable
	static NotificationManager provideNotificationManager(Context context) {
		return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Provides
	@Reusable
	static ConnectivityManager provideConnectivityManager(Context context) {
		return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Provides
	@Reusable
	static LocalBroadcastManager provideLocalBroadcastManager(Context context) {
		return LocalBroadcastManager.getInstance(context);
	}

}
