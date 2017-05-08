package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraDataModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraEvaluationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GeomagneticLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.KpIndexModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SunPositionModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		AuroraDataModule.class,
		AuroraEvaluationModule.class,
		GeomagneticLocationModule.class,
		GoogleLocationModule.class,
		KpIndexModule.class,
		SunPositionModule.class,
		WeatherModule.class
},
dependencies = {
		ApplicationComponent.class
})
@BackgroundScope
@SuppressWarnings("WeakerAccess")
public interface UpdaterComponent {
	void inject(Updater updater);
}
