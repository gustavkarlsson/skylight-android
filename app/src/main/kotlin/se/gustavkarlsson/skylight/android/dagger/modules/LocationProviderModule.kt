package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.location.GoogleLocationProvider

@Module
class LocationProviderModule {

    @Provides
    @Reusable
    fun provideLocationProvider(
            googleApiClient: GoogleApiClient
    ): LocationProvider = GoogleLocationProvider(googleApiClient)

    @Provides
    @Reusable
    fun provideGoogleApiLocationClient(
            context: Context
    ): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build()
    }
}
