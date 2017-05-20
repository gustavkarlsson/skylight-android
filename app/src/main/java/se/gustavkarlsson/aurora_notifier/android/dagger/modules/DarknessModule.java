package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.DarknessProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.KlausBrunnerDarknessProvider;

@Module
public abstract class DarknessModule {

	@Binds
	@Reusable
	abstract DarknessProvider bindDarknessProvider(KlausBrunnerDarknessProvider provider);

}
