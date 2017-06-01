package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.skylight.android.background.providers.DarknessProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.KlausBrunnerDarknessProvider;

@Module
public abstract class DarknessModule {

	@Binds
	@Reusable
	abstract DarknessProvider bindDarknessProvider(KlausBrunnerDarknessProvider provider);

}
