package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.feature.addplace.addPlaceModule
import se.gustavkarlsson.skylight.android.feature.background.backgroundModule
import se.gustavkarlsson.skylight.android.lib.analytics.analyticsModule
import se.gustavkarlsson.skylight.android.lib.darkness.darknessModule
import se.gustavkarlsson.skylight.android.lib.geomaglocation.geomagLocationModule
import se.gustavkarlsson.skylight.android.lib.kpindex.kpIndexModule
import se.gustavkarlsson.skylight.android.lib.location.locationModule
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.reverseGeocoderModule
import se.gustavkarlsson.skylight.android.lib.places.placesModule
import se.gustavkarlsson.skylight.android.lib.weather.weatherModule
import se.gustavkarlsson.skylight.android.modules.activityModule
import se.gustavkarlsson.skylight.android.modules.auroraReportModule
import se.gustavkarlsson.skylight.android.modules.evaluationModule
import se.gustavkarlsson.skylight.android.modules.formattingModule
import se.gustavkarlsson.skylight.android.modules.googlePlayServicesModule
import se.gustavkarlsson.skylight.android.modules.krateModule
import se.gustavkarlsson.skylight.android.modules.localizationModule
import se.gustavkarlsson.skylight.android.modules.navigationModule
import se.gustavkarlsson.skylight.android.modules.permissionsModule
import se.gustavkarlsson.skylight.android.modules.runVersionsModule
import se.gustavkarlsson.skylight.android.modules.settingsModule
import se.gustavkarlsson.skylight.android.modules.timeModule
import se.gustavkarlsson.skylight.android.modules.viewModelModule

val modules = listOf(
	settingsModule,
	backgroundModule,
	krateModule,
	runVersionsModule,
	googlePlayServicesModule,
	permissionsModule,
	auroraReportModule,
	timeModule,
	locationModule,
	reverseGeocoderModule,
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
	navigationModule,
	addPlaceModule,
	placesModule
) + extraModules
