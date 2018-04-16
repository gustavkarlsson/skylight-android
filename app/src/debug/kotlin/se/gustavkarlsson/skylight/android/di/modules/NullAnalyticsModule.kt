package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.Analytics

class NullAnalyticsModule : AnalyticsModule {
	override val analytics: Analytics = Analytics.NullAnalytics()
}
