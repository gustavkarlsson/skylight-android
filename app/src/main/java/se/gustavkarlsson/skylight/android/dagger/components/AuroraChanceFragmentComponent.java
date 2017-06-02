package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Subcomponent;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment;

@SuppressWarnings("WeakerAccess")
@Subcomponent(modules = FragmentRootViewModule.class)
@FragmentScope
public interface AuroraChanceFragmentComponent {
	void inject(AuroraChanceFragment auroraChanceFragment);
}
