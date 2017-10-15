package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

@Module
class CustomLocationProviderModule(
	private val locationProvider: LocationProvider
) {

    @Provides
    @Reusable
    fun provideLocationProvider(): LocationProvider = locationProvider
}
