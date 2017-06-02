package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;

@Component(dependencies = {
		ApplicationComponent.class
})
@SuppressWarnings("WeakerAccess")
@FragmentScope
public interface SettingsFragmentComponent {
	UpdateScheduler getUpdateScheduler();
}
