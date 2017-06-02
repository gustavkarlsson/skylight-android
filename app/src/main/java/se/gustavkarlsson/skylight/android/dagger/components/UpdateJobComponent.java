package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateJob;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.SystemServiceModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.BackgroundScope;

@Component(modules = {
		SystemServiceModule.class
}, dependencies = {
		ApplicationComponent.class
})
@BackgroundScope
@SuppressWarnings("WeakerAccess")
public interface UpdateJobComponent {
	UpdateJob getUpdateJob();
}
