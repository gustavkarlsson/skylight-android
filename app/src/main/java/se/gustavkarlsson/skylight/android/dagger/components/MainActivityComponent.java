package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Subcomponent;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.UpdateErrorBroadcastReceiverModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.SwipeToRefreshModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;

@SuppressWarnings("WeakerAccess")
@Subcomponent(modules = {
		SwipeToRefreshModule.class,
		UpdateErrorBroadcastReceiverModule.class
})
@ActivityScope
public interface MainActivityComponent {
	void inject(MainActivity mainActivity);

	AuroraChanceFragmentComponent getAuroraChanceFragmentComponent(FragmentRootViewModule fragmentRootViewModule);
	AuroraFactorsFragmentComponent getAuroraFactorsFragmentComponent(FragmentRootViewModule fragmentRootViewModule);
}
