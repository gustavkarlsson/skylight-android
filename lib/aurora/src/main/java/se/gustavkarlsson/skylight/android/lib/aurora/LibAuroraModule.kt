package se.gustavkarlsson.skylight.android.lib.aurora

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
object LibAuroraModule {

    @Provides
    internal fun chanceLevelFormatter(): Formatter<ChanceLevel> = ChanceLevelFormatter

    @Provides
    internal fun auroraReportProvider(impl: CombiningAuroraReportProvider): AuroraReportProvider = impl

    @Provides
    internal fun completeAuroraReportChanceEvaluator(
        impl: CompleteAuroraReportEvaluator,
    ): ChanceEvaluator<CompleteAuroraReport> = impl
}
