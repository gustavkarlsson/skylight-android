package se.gustavkarlsson.skylight.android.di.modules

import android.app.Application

class DebugSkylightAppModule(openWeatherMapApiKey: String, application: Application) :
	SkylightAppModule(openWeatherMapApiKey, application) {

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
