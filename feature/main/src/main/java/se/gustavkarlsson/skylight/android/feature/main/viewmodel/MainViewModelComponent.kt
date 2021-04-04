package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.utils.millis
import se.gustavkarlsson.skylight.android.feature.main.state.ContinuouslySearchAction
import se.gustavkarlsson.skylight.android.feature.main.state.PermissionsAction
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.feature.main.state.StreamCurrentLocationAction
import se.gustavkarlsson.skylight.android.feature.main.state.StreamPlacesAction
import se.gustavkarlsson.skylight.android.feature.main.state.StreamReportsLiveAction
import se.gustavkarlsson.skylight.android.feature.main.state.StreamSelectedPlaceAction
import se.gustavkarlsson.skylight.android.feature.main.state.StreamTriggerLevelsAction
import se.gustavkarlsson.skylight.android.feature.main.util.RelativeTimeFormatterModule
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.permissions.Permissions
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoderComponent
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent

@ViewModelScope
@Component(
    modules = [
        MainViewModelModule::class,
        RelativeTimeFormatterModule::class,
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
        GeomagLocationComponent::class,
        GeocoderComponent::class,
        ReverseGeocoderComponent::class,
        SettingsComponent::class,
    ]
)
internal interface MainViewModelComponent {
    @ExperimentalCoroutinesApi
    fun viewModel(): MainViewModel

    companion object {
        fun build(): MainViewModelComponent =
            DaggerMainViewModelComponent.builder()
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
                .geocoderComponent(GeocoderComponent.instance)
                .reverseGeocoderComponent(ReverseGeocoderComponent.instance)
                .settingsComponent(SettingsComponent.instance)
                .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SearchDelay

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class StreamThrottle

@Module
internal object MainViewModelModule {

    @Provides
    @SearchDelay
    fun provideSearchDelay(): Duration = 500.millis

    @Provides
    @StreamThrottle
    fun provideStreamThrottle(): Duration = 500.millis

    @ExperimentalCoroutinesApi
    @Provides
    fun startActions(
        permissionsAction: PermissionsAction,
        streamTriggerLevelsAction: StreamTriggerLevelsAction,
        streamSelectedPlaceAction: StreamSelectedPlaceAction,
        streamPlacesAction: StreamPlacesAction,
        continuouslySearchAction: ContinuouslySearchAction,
        streamReportsAction: StreamReportsLiveAction,
        streamCurrentLocationAction: StreamCurrentLocationAction,
    ): List<Action<State>> = listOf(
        permissionsAction,
        streamSelectedPlaceAction,
        streamPlacesAction,
        streamReportsAction,
        streamTriggerLevelsAction,
        continuouslySearchAction,
        streamCurrentLocationAction,
    )

    // TODO Load initial data, and then remove fallback for State.selectedPlace.
    //  or have another initial state
    // TODO Also remove initial permissions and get from checker instead
    @FlowPreview
    @ExperimentalCoroutinesApi
    @Provides
    fun initialState(
        selectedPlaceRepository: SelectedPlaceRepository,
    ): State = State(
        permissions = Permissions.INITIAL,
        currentLocationName = Loadable.Loading,
        selectedPlaceId = selectedPlaceRepository.get().id,
        selectedAuroraReport = LoadableAuroraReport.LOADING,
        search = Search.Inactive,
        places = emptyList(),
        notificationTriggerLevels = emptyMap(),
    )

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Provides
    @ViewModelScope
    fun store(
        initialState: State,
        startActions: List<@JvmSuppressWildcards Action<State>>,
    ): Store<State> = Store(initialState, startActions)
}
