package se.gustavkarlsson.skylight.android.lib.geocoder

import dagger.Module
import dagger.Provides

@Module
object LibGeocoderModule {

    @Provides
    internal fun geocoder(impl: MapboxGeocoder): Geocoder = impl
}
