package se.gustavkarlsson.skylight.android.di.components

import android.app.Application
import se.gustavkarlsson.skylight.android.di.modules.AnalyticsModule
import se.gustavkarlsson.skylight.android.di.modules.AuroraReportModule
import se.gustavkarlsson.skylight.android.di.modules.DebugAuroraReportModule
import se.gustavkarlsson.skylight.android.di.modules.NullAnalyticsModule

class DebugSkylightAppComponent(openWeatherMapApiKey: String, application: Application) :
	SkylightAppComponent(openWeatherMapApiKey, application) {

	override val analyticsModule: AnalyticsModule by lazy {
		NullAnalyticsModule()
	}

	override val auroraReportModule: AuroraReportModule by lazy {
		DebugAuroraReportModule(
			timeModule.timeProvider,
			locationModule.locationProvider,
			locationNameModule.locationNameProvider,
			darknessModule.darknessProvider,
			geomagLocationModule.geomagLocationProvider,
			kpIndexModule.kpIndexProvider,
			visibilityModule.visibilityProvider,
			locationNameModule.locationNameFlowable,
			kpIndexModule.kpIndexFlowable,
			geomagLocationModule.geomagLocationFlowable,
			darknessModule.darknessFlowable,
			visibilityModule.visibilityFlowable,
			contextModule.context,
			settingsModule.rxSharedPreferences
		)
	}
}
