package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module

@Module(includes = arrayOf(
		SystemServiceModule::class
))
class AppVisibilityEvaluatorModule
