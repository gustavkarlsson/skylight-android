package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.ReverseGeocoder

val libReverseGeocoderModule = module {

    single<ReverseGeocoder> {
        AndroidReverseGeocoder(
            geocoder = Geocoder(get()),
            retryDelay = 10.seconds
        )
    }
}

@Module
class LibReverseGeocoderModule {

    @Provides
    @Reusable
    internal fun reverseGeocoder(context: Context): ReverseGeocoder =
        AndroidReverseGeocoder(Geocoder(context), retryDelay = 10.seconds)
}
