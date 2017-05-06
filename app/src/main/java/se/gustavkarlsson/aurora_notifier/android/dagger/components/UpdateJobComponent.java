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

@Component(modules = {
		GoogleLocationModule.class,
		WeatherModule.class,
		KpIndexModule.class,
		SunPositionModule.class,
		GeomagneticLocationModule.class,
		AuroraDataModule.class,
		AuroraEvaluationModule.class
})
@SuppressWarnings("WeakerAccess")
public interface UpdateJobComponent {
	void inject(Updater updater);
}
