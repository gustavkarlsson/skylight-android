package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.di.modules.*

val appModule: AppModule by lazy {
	SkylightAppModule(
		BuildConfig.OPENWEATHERMAP_API_KEY,
		ApplicationContextModule(Skylight.instance),
		analyticsModule = NullAnalyticsModule(),
		auroraReportModule = null as DebugAuroraReportModule
	)
}
