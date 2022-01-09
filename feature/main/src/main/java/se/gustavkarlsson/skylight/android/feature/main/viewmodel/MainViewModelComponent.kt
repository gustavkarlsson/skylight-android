package se.gustavkarlsson.skylight.android.feature.main.viewmodel

import android.content.Context
import android.os.Build
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.utils.millis
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.state.ContinuouslySearchAction
import se.gustavkarlsson.skylight.android.feature.main.state.FinishLoadingAction
import se.gustavkarlsson.skylight.android.feature.main.state.LocationServiceStatusAction
import se.gustavkarlsson.skylight.android.feature.main.state.PermissionsAction
import se.gustavkarlsson.skylight.android.feature.main.state.Search
import se.gustavkarlsson.skylight.android.feature.main.state.State
import se.gustavkarlsson.skylight.android.feature.main.state.StreamCurrentLocationAction
import se.gustavkarlsson.skylight.android.feature.main.state.StreamCurrentLocationNameAction
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
import se.gustavkarlsson.skylight.android.lib.location.LocationServiceStatusProvider
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoderComponent
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent
import javax.inject.Qualifier

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
internal annotation class SearchThrottle

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class StreamThrottle

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class StayAlive

@Module
internal object MainViewModelModule {

    @Provides
    @ViewModelScope
    fun provideSearchChannel(): Channel<SearchFieldState> = Channel(Channel.CONFLATED)

    @Provides
    fun provideSearchSendChannel(channel: Channel<SearchFieldState>): SendChannel<SearchFieldState> = channel

    @Provides
    fun provideSearchReceiveChannel(channel: Channel<SearchFieldState>): ReceiveChannel<SearchFieldState> = channel

    @Provides
    @SearchThrottle
    fun provideSearchThrottle(): Duration = 500.millis

    @Provides
    @StreamThrottle
    fun provideStreamThrottle(): Duration = 500.millis

    @Provides
    @StayAlive
    fun provideStayAlive(): Duration = 1000.millis

    @Provides
    fun startActions(
        permissionsAction: PermissionsAction,
        locationServiceStatusAction: LocationServiceStatusAction,
        streamTriggerLevelsAction: StreamTriggerLevelsAction,
        streamSelectedPlaceAction: StreamSelectedPlaceAction,
        streamPlacesAction: StreamPlacesAction,
        streamCurrentLocationAction: StreamCurrentLocationAction,
        continuouslySearchAction: ContinuouslySearchAction,
        streamReportsAction: StreamReportsLiveAction,
        streamCurrentLocationNameAction: StreamCurrentLocationNameAction,
        finishLoadingAction: FinishLoadingAction,
    ): List<Action<State>> = listOf(
        permissionsAction,
        locationServiceStatusAction,
        streamSelectedPlaceAction,
        streamPlacesAction,
        streamCurrentLocationAction,
        streamReportsAction,
        streamTriggerLevelsAction,
        continuouslySearchAction,
        streamCurrentLocationNameAction,
        finishLoadingAction,
    )

    @Provides
    fun initialState(
        permissionChecker: PermissionChecker,
        locationServiceStatusProvider: LocationServiceStatusProvider,
    ): State = State.Loading(
        permissions = permissionChecker.permissions.value,
        locationServiceStatus = locationServiceStatusProvider.locationServiceStatus.value,
        currentLocation = Loading,
        currentLocationName = Loading,
        selectedPlace = null,
        selectedAuroraReport = LoadableAuroraReport.LOADING,
        search = Search.Inactive,
        places = null,
        settings = null,
    )

    @Provides
    @BackgroundLocationName
    fun provideBackgroundLocationName(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager.backgroundPermissionOptionLabel.toString()
        } else {
            context.getString(R.string.allow_all_the_time)
        }
    }

    @Provides
    @ViewModelScope
    fun store(
        initialState: State,
        startActions: List<@JvmSuppressWildcards Action<State>>,
    ): Store<State> = Store(initialState, startActions)
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BackgroundLocationName
