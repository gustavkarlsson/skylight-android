package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraFactorsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraReportModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.DarknessModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GeomagLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.KpIndexModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SettingsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		AuroraReportModule.class,
		GoogleLocationModule.class,
		AuroraFactorsModule.class,
		GeomagLocationModule.class,
		KpIndexModule.class,
		DarknessModule.class,
		WeatherModule.class,
		SettingsModule.class,
		SystemServiceModule.class
}, dependencies = {
		ApplicationComponent.class
})
@BackgroundScope
@ActivityScope
@SuppressWarnings("WeakerAccess")
public interface UpdaterComponent {
	AuroraReportProvider getAuroraReportProvider();
}
