package se.gustavkarlsson.skylight.android

import com.squareup.anvil.annotations.MergeComponent
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoderComponent
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent

@AppScope
@MergeComponent(AppScopeMarker::class)
internal interface ActualAppComponent :
    AppComponent,
    MainActivityComponent,
    AnalyticsComponent,
    BackgroundComponent,
    TimeComponent,
    SettingsComponent,
    RunVersionComponent,
    WeatherComponent,
    GeocoderComponent,
    ReverseGeocoderComponent,
    PlacesComponent,
    LocationComponent,
    AuroraComponent,
    PermissionsComponent,
    ScopedServiceComponent,
    NavigationComponent,
    DarknessComponent,
    KpIndexComponent,
    GeomagLocationComponent {

    fun moduleStarters(): Set<ModuleStarter>
}
