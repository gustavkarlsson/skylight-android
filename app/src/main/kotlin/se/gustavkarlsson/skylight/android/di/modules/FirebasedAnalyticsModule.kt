package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services_impl.FirebasedAnalytics

class FirebasedAnalyticsModule(contextModule: ContextModule) : AnalyticsModule {

	override val analytics: Analytics by lazy { FirebasedAnalytics(contextModule.context) }
}
