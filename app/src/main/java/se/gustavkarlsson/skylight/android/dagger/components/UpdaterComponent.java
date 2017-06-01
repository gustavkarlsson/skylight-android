package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.dagger.modules.prod.AuroraReportModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.skylight.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		AuroraReportModule.class
}, dependencies = {
		ApplicationComponent.class
})
@BackgroundScope
@ActivityScope
@SuppressWarnings("WeakerAccess")
public interface UpdaterComponent {
	AuroraReportProvider getAuroraReportProvider();
}
