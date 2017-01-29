package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.AuroraDataProviderImpl;

@Module
public abstract class AuroraDataModule {

	@Binds
	@Reusable
	abstract AuroraDataProvider bindAuroraDataProvider(AuroraDataProviderImpl provider);

}
