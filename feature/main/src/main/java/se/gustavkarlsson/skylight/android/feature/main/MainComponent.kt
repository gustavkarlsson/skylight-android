package se.gustavkarlsson.skylight.android.feature.main

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.conveyor.buildStore
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.feature.main.gui.MainViewModel
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.Weather
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent

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
    @ExperimentalCoroutinesApi
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
internal object MainModule {

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Provides
    fun mainStore(
        permissionChecker: PermissionChecker,
        selectedPlaceRepository: SelectedPlaceRepository,
        locationProvider: LocationProvider,
        auroraReportProvider: AuroraReportProvider
    ): Store<State> = buildStore(
        initialState = State(selectedPlace = selectedPlaceRepository.get()),
        openActions = listOf(LocationPermissionAction(permissionChecker.access)),
        liveActions = listOf(ToggleStreamAction(locationProvider.stream(), auroraReportProvider::stream)),
    )

    @ExperimentalCoroutinesApi
    @Provides
    fun viewModel(
        context: Context,
        mainStore: Store<State>,
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
            mainStore,
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
