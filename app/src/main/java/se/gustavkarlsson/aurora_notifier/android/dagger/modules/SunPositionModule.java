package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.KlausBrunnerSunPositionProvider;

@Module
public abstract class SunPositionModule {

	@Binds
	@Reusable
	abstract SunPositionProvider bindSunPositionProvider(KlausBrunnerSunPositionProvider provider);

}
