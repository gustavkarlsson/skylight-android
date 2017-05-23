package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.AuroraReportModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.DarknessModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GeomagActivityModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GeomagLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.VisibilityModule;
import se.gustavkarlsson.aurora_notifier.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;

@Component(modules = {
		SystemServiceModule.class,
		AuroraReportModule.class,
		GoogleLocationModule.class,
		GeomagLocationModule.class,
		GeomagActivityModule.class,
		DarknessModule.class,
		VisibilityModule.class
}, dependencies = {
		ApplicationComponent.class
})
@ActivityScope
@SuppressWarnings("WeakerAccess")
public interface MainActivityComponent {
	void inject(MainActivity mainActivity);
}
