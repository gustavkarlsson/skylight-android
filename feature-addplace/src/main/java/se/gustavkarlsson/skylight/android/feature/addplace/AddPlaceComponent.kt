package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.Main
import se.gustavkarlsson.skylight.android.ViewModelScope
import se.gustavkarlsson.skylight.android.lib.geocoder.Geocoder
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.utils.seconds

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
internal class AddPlaceModule {

    @Provides
    @ViewModelScope
    fun errorsRelay(): Relay<TextRef> = PublishRelay.create()

    @Provides
    @Reusable
    fun errorsObservable(relay: Relay<TextRef>): Observable<TextRef> = relay

    @Provides
    @Reusable
    fun errorsConsumer(relay: Relay<TextRef>): Consumer<TextRef> = relay

    @Provides
    fun knot(
        geocoder: Geocoder,
        errors: Consumer<TextRef>,
        @Main observeScheduler: Scheduler
    ): AddPlaceKnot = createKnot(geocoder, 1.seconds, errors, observeScheduler)
}
