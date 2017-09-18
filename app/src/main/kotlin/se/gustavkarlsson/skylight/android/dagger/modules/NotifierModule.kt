package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotifier

@Module(includes = arrayOf(
		ContextModule::class,
		SystemServiceModule::class,
		EvaluationModule::class
))
abstract class NotifierModule {

    @Binds
    @Reusable
    abstract fun bindNotifier(impl: AuroraReportNotifier): Notifier<AuroraReport>
}
