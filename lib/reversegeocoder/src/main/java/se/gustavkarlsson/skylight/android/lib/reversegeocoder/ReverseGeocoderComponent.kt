package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.CoroutineDispatcher
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import org.mobilenativefoundation.store.store5.StoreBuilder
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.Io
import java.util.Locale
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.time.Duration.Companion.seconds

@Component
@ReverseGeocoderScope
abstract class ReverseGeocoderComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
) {

    abstract val reverseGeocoder: ReverseGeocoder

    @Provides
    internal fun getAndroidGeocoder(
        context: Context,
        getLocale: () -> Locale,
    ): () -> Geocoder = {
        Geocoder(context, getLocale())
    }

    @Provides
    @ReverseGeocoderScope
    internal fun reverseGeocoder(
        getGeocoder: () -> Geocoder,
        @Io dispatcher: CoroutineDispatcher,
    ): ReverseGeocoder {
        val fetcher = createAndroidReverseGeocoderFetcher(getGeocoder, dispatcher)
        val store = StoreBuilder.from(fetcher)
            .build()
        return StoreReverseGeocoder(store, retryDelay = 10.seconds, approximationMeters = 1000.0)
    }

    companion object {
        val instance: ReverseGeocoderComponent = ReverseGeocoderComponent::class.create(
            coreComponent = CoreComponent.instance,
        )
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class ReverseGeocoderScope
