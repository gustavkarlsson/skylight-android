package se.gustavkarlsson.skylight.android.dagger.components;

import android.app.NotificationManager;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.background.Updater;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.EvaluationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.SystemServiceModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		SystemServiceModule.class,
		EvaluationModule.class
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
