package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.feature.about.aboutModule
import se.gustavkarlsson.skylight.android.feature.addplace.addPlaceModule
import se.gustavkarlsson.skylight.android.feature.background.backgroundModule
import se.gustavkarlsson.skylight.android.feature.googleplayservices.googlePlayServicesModule
import se.gustavkarlsson.skylight.android.feature.intro.introModule
import se.gustavkarlsson.skylight.android.feature.main.mainModule
import se.gustavkarlsson.skylight.android.feature.settings.settingsModule
import se.gustavkarlsson.skylight.android.lib.analytics.analyticsModule
import se.gustavkarlsson.skylight.android.lib.darkness.darknessModule
import se.gustavkarlsson.skylight.android.lib.geocoder.geocoderModule
import se.gustavkarlsson.skylight.android.lib.geomaglocation.geomagLocationModule
import se.gustavkarlsson.skylight.android.lib.kpindex.kpIndexModule
import se.gustavkarlsson.skylight.android.lib.location.locationModule
import se.gustavkarlsson.skylight.android.lib.places.placesModule
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.reverseGeocoderModule
import se.gustavkarlsson.skylight.android.lib.ui.uiModule
import se.gustavkarlsson.skylight.android.lib.weather.weatherModule
import se.gustavkarlsson.skylight.android.lib.settings.settingsModule as libSettingsModule

internal val modules = listOf(
	coreModule,
	appModule,
	settingsModule,
	backgroundModule,
	googlePlayServicesModule,
	uiModule,
	libSettingsModule,
	locationModule,
	reverseGeocoderModule,
	darknessModule,
	geomagLocationModule,
	kpIndexModule,
	weatherModule,
	analyticsModule,
	addPlaceModule,
	placesModule,
	geocoderModule,
	aboutModule,
	introModule,
	mainModule
)
