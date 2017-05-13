package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraEvaluationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraFactorsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GeomagneticLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.KpIndexModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SettingsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SunPositionModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		AuroraEvaluationModule.class,
		GoogleLocationModule.class,
		AuroraFactorsModule.class,
		GeomagneticLocationModule.class,
		KpIndexModule.class,
		SunPositionModule.class,
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
	AuroraEvaluationProvider getAuroraEvaluationProvider();
}
