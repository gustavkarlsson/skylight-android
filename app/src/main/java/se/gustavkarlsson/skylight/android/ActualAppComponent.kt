package se.gustavkarlsson.skylight.android

import dagger.Component
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.feature.background.FeatureBackgroundModule
import se.gustavkarlsson.skylight.android.feature.googleplayservices.FeatureGooglePlayServicesModule
import se.gustavkarlsson.skylight.android.feature.intro.FeatureIntroModule
import se.gustavkarlsson.skylight.android.feature.settings.FeatureSettingsModule
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.analytics.LibAnalyticsModule
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.aurora.LibAuroraModule
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.darkness.LibDarknessModule
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.geocoder.LibGeocoderModule
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.LibGeomagLocationModule
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.LibKpIndexModule
import se.gustavkarlsson.skylight.android.lib.location.LibLocationModule
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigationsetup.LibNavigationSetupModule
import se.gustavkarlsson.skylight.android.lib.navigationsetup.NavigationSetupComponent
import se.gustavkarlsson.skylight.android.lib.okhttp.LibOkHttpModule
import se.gustavkarlsson.skylight.android.lib.permissions.LibPermissionsModule
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.LibPlacesModule
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.LibReverseGeocoderModule
import se.gustavkarlsson.skylight.android.lib.runversion.LibRunVersionModule
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.LibScopedServiceModule
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.settings.LibSettingsModule
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.LibTimeModule
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.LibWeatherModule
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent
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
        LibRunVersionModule::class,
        LibScopedServiceModule::class,
        LibAuroraModule::class,
        FeatureGooglePlayServicesModule::class,
        FeatureIntroModule::class,
        FeatureSettingsModule::class,
        FeatureBackgroundModule::class
    ]
)
internal interface ActualAppComponent : AppComponent, AnalyticsComponent, BackgroundComponent,
    TimeComponent, SettingsComponent, RunVersionComponent, WeatherComponent, GeocoderComponent,
    PlacesComponent, LocationComponent, AuroraComponent, PermissionsComponent,
    NavigationSetupComponent, ScopedServiceComponent, NavigationComponent, DarknessComponent,
    KpIndexComponent, GeomagLocationComponent {

    fun moduleStarters(): Set<ModuleStarter>
}
