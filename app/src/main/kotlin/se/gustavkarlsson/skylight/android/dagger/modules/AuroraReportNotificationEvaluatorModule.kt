package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module

@Module(includes = arrayOf(
        EvaluationModule::class,
        SettingsModule::class
))
abstract class AuroraReportNotificationEvaluatorModule
