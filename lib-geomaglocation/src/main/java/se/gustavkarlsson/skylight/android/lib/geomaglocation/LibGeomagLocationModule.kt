package se.gustavkarlsson.skylight.android.lib.geomaglocation

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Single
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Geocoder
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.Time
import java.util.Locale

val libGeomagLocationModule = module {

    single<GeomagLocationProvider> {
        GeomagLocationProviderImpl(time = get())
    }
}

@Module
class LibGeomagLocationModule {

    @Provides
    @Reusable
    internal fun geomagLocationProvider(time: Time): GeomagLocationProvider =
        GeomagLocationProviderImpl(time)
}
