package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ContextModule;

@Component(modules = {
		ContextModule.class
})
@SuppressWarnings("WeakerAccess")
public interface SettingsFragmentComponent {
	UpdateScheduler getUpdateScheduler();
}
