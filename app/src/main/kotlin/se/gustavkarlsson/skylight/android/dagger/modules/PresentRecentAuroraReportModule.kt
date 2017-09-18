package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentRecentAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.ProvideRecentAuroraReportToPublisher

@Module
abstract class PresentRecentAuroraReportModule {

    @Binds
    @Reusable
    abstract fun bindProvideRecentAuroraReportToPublisher(impl: ProvideRecentAuroraReportToPublisher): PresentRecentAuroraReport
}
