package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class LocalBroadcastManagerModule {
	private final Context context;

	public LocalBroadcastManagerModule(Context context) {
		this.context = context;
	}

	@Provides
	@Reusable
	LocalBroadcastManager provideLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(context);
	}
}
