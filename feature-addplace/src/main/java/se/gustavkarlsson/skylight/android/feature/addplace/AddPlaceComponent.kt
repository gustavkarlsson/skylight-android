package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.services.PlacesRepository

@Component(
    modules = [AddPlaceModule::class],
    dependencies = [
        AppComponent::class,
        GeocoderComponent::class
    ]
)
internal interface AddPlaceComponent {
    fun viewModel(): AddPlaceViewModel

    companion object {
        fun build(): AddPlaceComponent =
            DaggerAddPlaceComponent.builder()
                .appComponent(AppComponent.instance)
                .geocoderComponent(GeocoderComponent.instance)
                .build()
    }
}

@Module
internal class AddPlaceModule {

    @Provides
    fun viewModel(
        placesRepository: PlacesRepository,
        geocoder: Geocoder
    ): AddPlaceViewModel {
        val errorsRelay = PublishRelay.create<TextRef>()
        val knot = createKnot(geocoder, 1.seconds, errorsRelay)

        return AddPlaceViewModel(
            placesRepository = placesRepository,
            knot = knot,
            errorMessages = errorsRelay
        )
    }
}
