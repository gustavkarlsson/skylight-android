package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors.AggregatingAuroraFactorsProvider;

@Module
public abstract class AuroraFactorsModule {

	@Binds
	@Reusable
	abstract AuroraFactorsProvider bindAuroraFactorsProvider(AggregatingAuroraFactorsProvider provider);

}
