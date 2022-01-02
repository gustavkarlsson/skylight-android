package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.content.Context
import android.location.Geocoder
import com.dropbox.android.external.store4.StoreBuilder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.utils.seconds

@Module
object LibReverseGeocoderModule {

    @Provides
    @Reusable
    internal fun reverseGeocoder(context: Context, @Io dispatcher: CoroutineDispatcher): ReverseGeocoder {
        val fetcher = createAndroidReverseGeocoderFetcher(Geocoder(context), dispatcher)
        val store = StoreBuilder.from(fetcher)
            .build()
        return StoreReverseGeocoder(store, retryDelay = 10.seconds, approximationMeters = 1000.0)
    }
}
