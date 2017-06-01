package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.dagger.modules.AuroraReportModule;
import se.gustavkarlsson.skylight.android.dagger.modules.DarknessModule;
import se.gustavkarlsson.skylight.android.dagger.modules.EvaluationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.GeomagActivityModule;
import se.gustavkarlsson.skylight.android.dagger.modules.GeomagLocationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.NotificationOutdatedModule;
import se.gustavkarlsson.skylight.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.skylight.android.dagger.modules.VisibilityModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;

@Component(modules = {
		SystemServiceModule.class,
		AuroraReportModule.class,
		GoogleLocationModule.class,
		GeomagLocationModule.class,
		GeomagActivityModule.class,
		DarknessModule.class,
		VisibilityModule.class,
		EvaluationModule.class,
		NotificationOutdatedModule.class
}, dependencies = {
		ApplicationComponent.class
})
@ActivityScope
@SuppressWarnings("WeakerAccess")
public interface MainActivityComponent {
	void inject(MainActivity mainActivity);
}
