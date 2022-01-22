package se.gustavkarlsson.skylight.android.lib.aurora

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
object LibAuroraModule {

    @Provides
    @Reusable
    internal fun chanceLevelFormatter(): Formatter<ChanceLevel> = ChanceLevelFormatter

    @Provides
    @Reusable
    internal fun auroraReportProvider(impl: CombiningAuroraReportProvider): AuroraReportProvider = impl

    @Provides
    @Reusable
    internal fun completeAuroraReportChanceEvaluator(
        impl: CompleteAuroraReportEvaluator,
    ): ChanceEvaluator<CompleteAuroraReport> = impl
}
