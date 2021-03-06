package se.gustavkarlsson.skylight.android.lib.geocoder

import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import se.gustavkarlsson.skylight.android.core.Io
import java.util.Locale

@Module
object LibGeocoderModule {

    @Provides
    @Reusable
    internal fun geocoder(getLocale: () -> Locale, @Io dispatcher: CoroutineDispatcher): Geocoder =
        MapboxGeocoder(
            accessToken = BuildConfig.MAPBOX_API_KEY,
            getLocale = getLocale,
            dispatcher = dispatcher
        )
}
