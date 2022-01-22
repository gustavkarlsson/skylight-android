package se.gustavkarlsson.skylight.android.initializers

import android.app.Application
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import se.gustavkarlsson.skylight.android.AppModule
import se.gustavkarlsson.skylight.android.DaggerActualAppComponent
import se.gustavkarlsson.skylight.android.MainActivityComponent
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationModule
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoderComponent
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent
import se.gustavkarlsson.skylight.android.navigation.DefaultScreens

private object Setter :
    AppComponent.Setter,
    AnalyticsComponent.Setter,
    BackgroundComponent.Setter,
    TimeComponent.Setter,
    SettingsComponent.Setter,
    RunVersionComponent.Setter,
    WeatherComponent.Setter,
    GeocoderComponent.Setter,
    ReverseGeocoderComponent.Setter,
    PlacesComponent.Setter,
    LocationComponent.Setter,
    AuroraComponent.Setter,
    PermissionsComponent.Setter,
    ScopedServiceComponent.Setter,
    NavigationComponent.Setter,
    DarknessComponent.Setter,
    KpIndexComponent.Setter,
    GeomagLocationComponent.Setter,
    MainActivityComponent.Setter

internal fun Application.initDagger() {
    val component = DaggerActualAppComponent.builder()
        .appModule(AppModule(this))
        .navigationModule(NavigationModule(DefaultScreens))
        .build()

    with(Setter) {
        setAppComponent(component)
        setMainActivityComponent(component)
        setAnalyticsComponent(component)
        setBackgroundComponent(component)
        setTimeComponent(component)
        setSettingsComponent(component)
        setRunVersionComponent(component)
        setWeatherComponent(component)
        setGeocoderComponent(component)
        setReverseGeocoderComponent(component)
        setPlacesComponent(component)
        setLocationComponent(component)
        setAuroraComponent(component)
        setPermissionsComponent(component)
        setScopedServiceComponent(component)
        setNavigationComponent(component)
        setDarknessComponent(component)
        setKpIndexComponent(component)
        setGeomagLocationComponent(component)
    }

    runBlocking {
        component.moduleStarters()
            .map { starter ->
                launch { starter.start() }
            }
            .joinAll()
    }
}
