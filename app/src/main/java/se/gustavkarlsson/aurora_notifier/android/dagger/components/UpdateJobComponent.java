package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import android.app.NotificationManager;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.EvaluationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.BackgroundScope;

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
