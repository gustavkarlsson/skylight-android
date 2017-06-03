package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Subcomponent;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.DarknessPresenterModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.GeomagActivityPresenterModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.GeomagLocationPresenterModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.VisibilityPresenterModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorFragment;

@SuppressWarnings("WeakerAccess")
@Subcomponent(modules = {
		FragmentRootViewModule.class,
		GeomagActivityPresenterModule.class,
		GeomagLocationPresenterModule.class,
		DarknessPresenterModule.class,
		VisibilityPresenterModule.class
})
@FragmentScope
public interface AuroraFactorsFragmentComponent {
	void inject(AuroraFactorFragment auroraFactorFragment);
}
