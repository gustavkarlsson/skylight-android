package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class GeocoderModule {

    @Provides
    @Reusable
    fun provideGeocoder(
            context: Context
    ): Geocoder = Geocoder(context)
}
