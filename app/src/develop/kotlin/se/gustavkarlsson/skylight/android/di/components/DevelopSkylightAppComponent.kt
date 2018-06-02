package se.gustavkarlsson.skylight.android.di.components

import android.app.Application
import se.gustavkarlsson.skylight.android.di.modules.AnalyticsModule
import se.gustavkarlsson.skylight.android.di.modules.AuroraReportModule
import se.gustavkarlsson.skylight.android.di.modules.DevelopAuroraReportModule
import se.gustavkarlsson.skylight.android.di.modules.NullAnalyticsModule

class DevelopSkylightAppComponent(application: Application) :
	SkylightAppComponent(application) {

	override val analyticsModule: AnalyticsModule by lazy {
		NullAnalyticsModule()
	}

	override val auroraReportModule: AuroraReportModule by lazy {
		DevelopAuroraReportModule(
			timeModule,
			locationModule,
			locationNameModule,
			darknessModule,
			geomagLocationModule,
			kpIndexModule,
			weatherModule,
			contextModule,
			rxSharedPreferencesModule
		)
	}
}
