package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.dagger.modules.prod.AuroraReportModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;

@Component(modules = {
		AuroraReportModule.class
}, dependencies = {
		ApplicationComponent.class
})
@ActivityScope
@SuppressWarnings("WeakerAccess")
public interface MainActivityComponent {
	void inject(MainActivity mainActivity);
}
