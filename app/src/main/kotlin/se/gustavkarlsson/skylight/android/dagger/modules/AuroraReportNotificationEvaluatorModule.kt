package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module

@Module(includes = arrayOf(
        LatestAuroraReportCacheModule::class,
        EvaluationModule::class,
        SettingsModule::class,
        ReportOutdatedEvaluatorModule::class,
        AppVisibilityEvaluatorModule::class
))
abstract class AuroraReportNotificationEvaluatorModule
