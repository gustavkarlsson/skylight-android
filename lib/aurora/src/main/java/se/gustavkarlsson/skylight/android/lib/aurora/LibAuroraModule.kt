package se.gustavkarlsson.skylight.android.lib.aurora

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
@ContributesTo(AppScopeMarker::class)
object LibAuroraModule {

    @Provides
    internal fun chanceLevelFormatter(): Formatter<ChanceLevel> = ChanceLevelFormatter

    @Provides
    internal fun auroraReportProvider(impl: CombiningAuroraReportProvider): AuroraReportProvider = impl

    @Provides
    internal fun auroraForecastReportProvider(
        impl: CombiningAuroraForecastReportProvider,
    ): AuroraForecastReportProvider = impl

    @Provides
    internal fun auroraReportChanceEvaluator(
        impl: AuroraReportEvaluator,
    ): ChanceEvaluator<AuroraReport> = impl
}
