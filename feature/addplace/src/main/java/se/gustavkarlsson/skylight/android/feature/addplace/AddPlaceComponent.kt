package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import se.gustavkarlsson.conveyor.Store
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.core.utils.seconds
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent

@ViewModelScope
@Component(
    modules = [AddPlaceModule::class],
    dependencies = [
        AppComponent::class,
        GeocoderComponent::class,
        PlacesComponent::class
    ]
)
internal interface AddPlaceComponent {
    @ExperimentalCoroutinesApi
    @FlowPreview
    fun viewModel(): AddPlaceViewModel

    companion object {
        fun build(): AddPlaceComponent =
            DaggerAddPlaceComponent.builder()
                .appComponent(AppComponent.instance)
                .geocoderComponent(GeocoderComponent.instance)
                .placesComponent(PlacesComponent.instance)
                .build()
    }
}

@Module
internal object AddPlaceModule {

    @ExperimentalCoroutinesApi
    @FlowPreview
    @Provides
    @JvmSuppressWildcards
    fun store(
        geocoder: Geocoder,
    ): Store<State> = Store(
        initialState = State(),
        startActions = listOf(ContinuouslySearchAction(geocoder, 1.seconds))
    )
}
