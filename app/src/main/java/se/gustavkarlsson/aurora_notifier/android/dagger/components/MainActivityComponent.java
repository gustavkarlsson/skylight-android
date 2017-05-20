package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraFactorsModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraReportModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.DarknessModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GeomagLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.KpIndexModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.UpdaterModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.WeatherModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;

@Component(modules = {
		SystemServiceModule.class,
		AuroraReportModule.class,
		GoogleLocationModule.class,
		AuroraFactorsModule.class,
		GeomagLocationModule.class,
		KpIndexModule.class,
		DarknessModule.class,
		WeatherModule.class,
		UpdaterModule.class
}, dependencies = {
		ApplicationComponent.class
})
@ActivityScope
@SuppressWarnings("WeakerAccess")
public interface MainActivityComponent {
	void inject(MainActivity mainActivity);
}
