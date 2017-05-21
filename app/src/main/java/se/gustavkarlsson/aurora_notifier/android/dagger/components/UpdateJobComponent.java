package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import android.app.NotificationManager;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SettingsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		SystemServiceModule.class,
		SettingsModule.class
}, dependencies = {
		ApplicationComponent.class
})
@BackgroundScope
@SuppressWarnings("WeakerAccess")
public interface UpdateJobComponent {
	NotificationManager getNotificationManager();
	UpdateScheduler getUpdateScheduler();
	Updater getUpdater();
}
