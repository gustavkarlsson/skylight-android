package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.content.Context
import android.location.Geocoder
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.Io
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Module
@ContributesTo(AppScopeMarker::class)
object LibReverseGeocoderModule {

    @Provides
    internal fun getGeocoder(
        context: Context,
        getLocale: () -> Locale,
    ): () -> Geocoder = {
        Geocoder(context, getLocale())
    }

    @Provides
    @Reusable
    internal fun reverseGeocoder(
        getGeocoder: () -> Geocoder,
        @Io dispatcher: CoroutineDispatcher,
    ): ReverseGeocoder {
        val fetcher = createAndroidReverseGeocoderFetcher(getGeocoder, dispatcher)
        val store = StoreBuilder.from(fetcher)
            .build()
        return StoreReverseGeocoder(store, retryDelay = 10.seconds, approximationMeters = 1000.0)
    }
}
