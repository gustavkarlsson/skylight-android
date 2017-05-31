package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.dagger.modules.AuroraReportModule;
import se.gustavkarlsson.skylight.android.dagger.modules.DarknessModule;
import se.gustavkarlsson.skylight.android.dagger.modules.GeomagActivityModule;
import se.gustavkarlsson.skylight.android.dagger.modules.GeomagLocationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.GoogleLocationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.SystemServiceModule;
import se.gustavkarlsson.skylight.android.dagger.modules.VisibilityModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.skylight.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		AuroraReportModule.class,
		GoogleLocationModule.class,
		GeomagLocationModule.class,
		GeomagActivityModule.class,
		DarknessModule.class,
		VisibilityModule.class,
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
