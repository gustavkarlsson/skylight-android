package se.gustavkarlsson.skylight.android

import dagger.Component
import se.gustavkarlsson.skylight.android.feature.background.FeatureBackgroundModule
import se.gustavkarlsson.skylight.android.feature.googleplayservices.FeatureGooglePlayServicesModule
import se.gustavkarlsson.skylight.android.feature.intro.FeatureIntroModule
import se.gustavkarlsson.skylight.android.feature.settings.FeatureSettingsModule
import se.gustavkarlsson.skylight.android.lib.analytics.LibAnalyticsModule
import se.gustavkarlsson.skylight.android.lib.aurora.LibAuroraModule
import se.gustavkarlsson.skylight.android.lib.darkness.LibDarknessModule
import se.gustavkarlsson.skylight.android.lib.geocoder.LibGeocoderModule
import se.gustavkarlsson.skylight.android.lib.geomaglocation.LibGeomagLocationModule
import se.gustavkarlsson.skylight.android.lib.kpindex.LibKpIndexModule
import se.gustavkarlsson.skylight.android.lib.location.LibLocationModule
import se.gustavkarlsson.skylight.android.lib.navigationsetup.LibNavigationSetupModule
import se.gustavkarlsson.skylight.android.lib.okhttp.LibOkHttpModule
import se.gustavkarlsson.skylight.android.lib.permissions.LibPermissionsModule
import se.gustavkarlsson.skylight.android.lib.places.LibPlacesModule
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.LibReverseGeocoderModule
import se.gustavkarlsson.skylight.android.lib.scopedservice.LibScopedServiceModule
import se.gustavkarlsson.skylight.android.lib.settings.LibSettingsModule
import se.gustavkarlsson.skylight.android.lib.time.LibTimeModule
import se.gustavkarlsson.skylight.android.lib.weather.LibWeatherModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        LibTimeModule::class,
        LibOkHttpModule::class,
        LibWeatherModule::class,
        LibAnalyticsModule::class,
        LibDarknessModule::class,
        LibGeocoderModule::class,
        LibGeomagLocationModule::class,
        LibKpIndexModule::class,
        LibLocationModule::class,
        LibPlacesModule::class,
        LibReverseGeocoderModule::class,
        LibSettingsModule::class,
        LibNavigationSetupModule::class,
        LibPermissionsModule::class,
        LibScopedServiceModule::class,
        LibAuroraModule::class,
        FeatureGooglePlayServicesModule::class,
        FeatureIntroModule::class,
        FeatureSettingsModule::class,
        FeatureBackgroundModule::class
    ]
)
internal interface ActualAppComponent : AppComponent
