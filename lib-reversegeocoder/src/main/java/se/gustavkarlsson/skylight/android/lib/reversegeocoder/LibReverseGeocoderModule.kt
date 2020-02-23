package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.ReverseGeocoder

@Module
class LibReverseGeocoderModule {

    @Provides
    @Reusable
    internal fun reverseGeocoder(context: Context): ReverseGeocoder =
        AndroidReverseGeocoder(Geocoder(context), retryDelay = 10.seconds)
}
