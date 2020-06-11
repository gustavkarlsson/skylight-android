package se.gustavkarlsson.skylight.android.feature.main

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import de.halfbit.knot.Knot
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.feature.main.gui.MainViewModel
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.utils.minutes

@Component(
    modules = [
        MainModule::class
    ],
    dependencies = [
        AppComponent::class,
        TimeComponent::class,
        WeatherComponent::class,
        LocationComponent::class,
        AuroraComponent::class,
        PermissionsComponent::class,
        PlacesComponent::class,
        DarknessComponent::class,
        KpIndexComponent::class,
        GeomagLocationComponent::class
    ]
)
internal interface MainComponent {
    fun viewModel(): MainViewModel

    companion object {
        fun build(): MainComponent =
            DaggerMainComponent.builder()
                .appComponent(AppComponent.instance)
                .timeComponent(TimeComponent.instance)
                .weatherComponent(WeatherComponent.instance)
                .locationComponent(LocationComponent.instance)
                .auroraComponent(AuroraComponent.instance)
                .permissionsComponent(PermissionsComponent.instance)
                .placesComponent(PlacesComponent.instance)
                .darknessComponent(DarknessComponent.instance)
                .kpIndexComponent(KpIndexComponent.instance)
                .geomagLocationComponent(GeomagLocationComponent.instance)
                .build()
    }
}

@Module
internal class MainModule {

    @Provides
    fun mainKnot(
        permissionChecker: PermissionChecker,
        selectedPlaceRepository: SelectedPlaceRepository,
        locationProvider: LocationProvider,
        auroraReportProvider: AuroraReportProvider
    ): Knot<State, Change> = buildMainKnot(
        permissionChecker,
        selectedPlaceRepository,
        locationProvider,
        auroraReportProvider
    )

    @Provides
    fun viewModel(
        context: Context,
        mainKnot: Knot<State, Change>,
        time: Time,
        auroraChanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
        chanceLevelFormatter: Formatter<ChanceLevel>,
        kpIndexEvaluator: ChanceEvaluator<KpIndex>,
        kpIndexFormatter: Formatter<KpIndex>,
        geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
        geomagLocationFormatter: Formatter<GeomagLocation>,
        weatherEvaluator: ChanceEvaluator<Weather>,
        weatherFormatter: Formatter<Weather>,
        darknessEvaluator: ChanceEvaluator<Darkness>,
        darknessFormatter: Formatter<Darkness>,
        locationPermissionChecker: PermissionChecker
    ): MainViewModel {
        val rightNowText = context.getString(R.string.right_now)
        val relativeTimeFormatter = DateUtilsRelativeTimeFormatter(rightNowText)
        return MainViewModel(
            mainKnot,
            auroraChanceEvaluator,
            relativeTimeFormatter,
            chanceLevelFormatter,
            darknessEvaluator,
            darknessFormatter,
            geomagLocationEvaluator,
            geomagLocationFormatter,
            kpIndexEvaluator,
            kpIndexFormatter,
            weatherEvaluator,
            weatherFormatter,
            locationPermissionChecker,
            ChanceToColorConverter(context),
            time,
            1.minutes
        )
    }
}
