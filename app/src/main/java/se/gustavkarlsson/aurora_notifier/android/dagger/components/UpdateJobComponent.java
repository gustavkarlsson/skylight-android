package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import android.app.NotificationManager;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraDataModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraEvaluationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GeomagneticLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.KpIndexModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SettingsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SunPositionModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.UpdaterModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		SystemServiceModule.class,
		AuroraEvaluationModule.class,
		GoogleLocationModule.class,
		AuroraDataModule.class,
		GeomagneticLocationModule.class,
		KpIndexModule.class,
		SunPositionModule.class,
		WeatherModule.class,
		SettingsModule.class,
		UpdaterModule.class
}, dependencies = {
		ApplicationComponent.class
})
@BackgroundScope
@SuppressWarnings("WeakerAccess")
public interface UpdateJobComponent {
	NotificationManager getNotificationManager();
	UpdateScheduler getUpdateScheduler();
	Updater getUpdater();
	AuroraEvaluationProvider getAuroraEvaluationProvider();
}
