package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.analytics.analyticsModule
import se.gustavkarlsson.skylight.android.background.backgroundModule
import se.gustavkarlsson.skylight.android.location.locationModule
import se.gustavkarlsson.skylight.android.modules.activityModule
import se.gustavkarlsson.skylight.android.modules.auroraReportModule
import se.gustavkarlsson.skylight.android.modules.connectivityModule
import se.gustavkarlsson.skylight.android.modules.darknessModule
import se.gustavkarlsson.skylight.android.modules.evaluationModule
import se.gustavkarlsson.skylight.android.modules.formattingModule
import se.gustavkarlsson.skylight.android.modules.geomagLocationModule
import se.gustavkarlsson.skylight.android.modules.googlePlayServicesModule
import se.gustavkarlsson.skylight.android.modules.kpIndexModule
import se.gustavkarlsson.skylight.android.modules.krateModule
import se.gustavkarlsson.skylight.android.modules.localizationModule
import se.gustavkarlsson.skylight.android.modules.locationNameModule
import se.gustavkarlsson.skylight.android.modules.navigationModule
import se.gustavkarlsson.skylight.android.modules.permissionsModule
import se.gustavkarlsson.skylight.android.modules.runVersionsModule
import se.gustavkarlsson.skylight.android.modules.settingsModule
import se.gustavkarlsson.skylight.android.modules.timeModule
import se.gustavkarlsson.skylight.android.modules.viewModelModule
import se.gustavkarlsson.skylight.android.weather.weatherModule

val modules = listOf(
	settingsModule,
	backgroundModule,
	connectivityModule,
	krateModule,
	runVersionsModule,
	googlePlayServicesModule,
	permissionsModule,
	auroraReportModule,
	timeModule,
	locationModule,
	locationNameModule,
	darknessModule,
	geomagLocationModule,
	kpIndexModule,
	weatherModule,
	viewModelModule,
	evaluationModule,
	formattingModule,
	localizationModule,
	analyticsModule,
	activityModule,
	navigationModule
) + extraModules
