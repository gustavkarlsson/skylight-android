package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.feature.about.featureAboutModule
import se.gustavkarlsson.skylight.android.feature.addplace.featureAddPlaceModule
import se.gustavkarlsson.skylight.android.feature.background.featureBackgroundModule
import se.gustavkarlsson.skylight.android.feature.googleplayservices.featureGooglePlayServicesModule
import se.gustavkarlsson.skylight.android.feature.intro.featureIntroModule
import se.gustavkarlsson.skylight.android.feature.main.featureMainModule
import se.gustavkarlsson.skylight.android.feature.settings.featureSettingsModule
import se.gustavkarlsson.skylight.android.lib.analytics.libAnalyticsModule
import se.gustavkarlsson.skylight.android.lib.darkness.libDarknessModule
import se.gustavkarlsson.skylight.android.lib.geocoder.libGeocoderModule
import se.gustavkarlsson.skylight.android.lib.geomaglocation.libGeomagLocationModule
import se.gustavkarlsson.skylight.android.lib.kpindex.libKpIndexModule
import se.gustavkarlsson.skylight.android.lib.location.libLocationModule
import se.gustavkarlsson.skylight.android.lib.navigation.libNavigationModule
import se.gustavkarlsson.skylight.android.lib.okhttp.libOkHttpModule
import se.gustavkarlsson.skylight.android.lib.permissions.libPermissionsModule
import se.gustavkarlsson.skylight.android.lib.places.libPlacesModule
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.libReverseGeocoderModule
import se.gustavkarlsson.skylight.android.lib.weather.libWeatherModule
import se.gustavkarlsson.skylight.android.lib.settings.libSettingsModule as libSettingsModule

internal val modules = listOf(
    coreModule,
    libSettingsModule,
    libLocationModule,
    libReverseGeocoderModule,
    libDarknessModule,
    libGeomagLocationModule,
    libOkHttpModule,
    libKpIndexModule,
    libNavigationModule,
    libWeatherModule,
    libAnalyticsModule,
    libPermissionsModule,
    libPlacesModule,
    libGeocoderModule,
    featureSettingsModule,
    featureBackgroundModule,
    featureGooglePlayServicesModule,
    featureAddPlaceModule,
    featureAboutModule,
    featureIntroModule,
    featureMainModule,
    appModule
)
