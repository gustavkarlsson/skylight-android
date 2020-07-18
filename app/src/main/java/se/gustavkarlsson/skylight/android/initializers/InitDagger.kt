package se.gustavkarlsson.skylight.android.initializers

import android.app.Application
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.AppModule
import se.gustavkarlsson.skylight.android.DaggerActualAppComponent
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigationsetup.NavigationSetupComponent
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent
import se.gustavkarlsson.skylight.android.utils.allowDiskReadsAndWritesInStrictMode

private object Setter :
    AppComponent.Setter,
    AnalyticsComponent.Setter,
    BackgroundComponent.Setter,
    TimeComponent.Setter,
    SettingsComponent.Setter,
    RunVersionComponent.Setter,
    WeatherComponent.Setter,
    GeocoderComponent.Setter,
    PlacesComponent.Setter,
    LocationComponent.Setter,
    AuroraComponent.Setter,
    PermissionsComponent.Setter,
    NavigationSetupComponent.Setter,
    ScopedServiceComponent.Setter,
    NavigationComponent.Setter,
    DarknessComponent.Setter,
    KpIndexComponent.Setter,
    GeomagLocationComponent.Setter

internal fun Application.initDagger() {
    val component = DaggerActualAppComponent.builder()
        .appModule(AppModule(this))
        .build()

    with(Setter) {
        setAppComponent(component)
        setAnalyticsComponent(component)
        setBackgroundComponent(component)
        setTimeComponent(component)
        setSettingsComponent(component)
        setRunVersionComponent(component)
        setWeatherComponent(component)
        setGeocoderComponent(component)
        setPlacesComponent(component)
        setLocationComponent(component)
        setAuroraComponent(component)
        setPermissionsComponent(component)
        setNavigationSetupComponent(component)
        setScopedServiceComponent(component)
        setNavigationComponent(component)
        setDarknessComponent(component)
        setKpIndexComponent(component)
        setGeomagLocationComponent(component)
    }

    allowDiskReadsAndWritesInStrictMode {
        component.moduleStarters().forEach(ModuleStarter::start)
    }
}
