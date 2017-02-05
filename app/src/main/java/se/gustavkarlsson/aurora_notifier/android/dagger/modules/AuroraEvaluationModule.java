package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.AuroraEvaluationProviderImpl;

@Module
public abstract class AuroraEvaluationModule {

	@Binds
	@Reusable
	abstract AuroraEvaluationProvider bindAuroraEvaluationProvider(AuroraEvaluationProviderImpl provider);

}
