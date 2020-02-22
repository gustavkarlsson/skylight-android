package se.gustavkarlsson.skylight.android.lib.geocoder

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Single
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Geocoder
import java.util.Locale

val libGeocoderModule = module {

    single<Geocoder> {
        val locale = get<Single<Locale>>("locale")
        MapboxGeocoder(
            accessToken = BuildConfig.MAPBOX_API_KEY,
            getLocale = locale::blockingGet
        )
    }
}

@Module
class LibGeocoderModule {

    @Provides
    @Reusable
    internal fun geocoder(singleLocale: Single<Locale>): Geocoder =
        MapboxGeocoder(
            accessToken = BuildConfig.MAPBOX_API_KEY,
            getLocale = singleLocale::blockingGet
        )
}
