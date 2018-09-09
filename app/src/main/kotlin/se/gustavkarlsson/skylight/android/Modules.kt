package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.background.backgroundModule
import se.gustavkarlsson.skylight.android.modules.*

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
	activityModule
) + extraModules
