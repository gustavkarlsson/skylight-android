package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services_impl.FirebasedAnalytics

class FirebasedAnalyticsModule(context: Context) : AnalyticsModule {
	override val analytics: Analytics by lazy { FirebasedAnalytics(context) }
}
