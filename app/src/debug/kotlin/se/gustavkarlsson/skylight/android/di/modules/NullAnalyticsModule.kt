package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services_impl.NullAnalytics

class NullAnalyticsModule : AnalyticsModule {
	override val analytics: Analytics = NullAnalytics()
}
