package se.gustavkarlsson.skylight.android.lib.geocoder

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Single
import java.util.Locale

@Module
object LibGeocoderModule {

    @Provides
    @Reusable
    internal fun geocoder(singleLocale: Single<Locale>): Geocoder =
        MapboxGeocoder(
            accessToken = BuildConfig.MAPBOX_API_KEY,
            getLocale = singleLocale::blockingGet
        )
}
