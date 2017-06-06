package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.background.Updater;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;

import static se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_ERROR_NAME;

@Module
public abstract class UpdateErrorBroadcastReceiverModule {

	@Provides
	@Named(UPDATE_ERROR_NAME)
	@ActivityScope
	static BroadcastReceiver provideUpdateErrorBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (Updater.Companion.getRESPONSE_UPDATE_ERROR().equals(action)) {
					String message = intent.getStringExtra(Updater.Companion.getRESPONSE_UPDATE_ERROR_EXTRA_MESSAGE());
					Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				}
			}
		};
	}

}
