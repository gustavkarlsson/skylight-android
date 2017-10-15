package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.AndroidLocationProvider

@Module
class LocationProviderModule {

    @Provides
    @Reusable
    fun provideLocationProvider(
            context: Context
    ): LocationProvider = AndroidLocationProvider(LocationServices.getFusedLocationProviderClient(context))
}
