package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

@Module
class CustomLocationNameProviderModule(
	private val locationNameProvider: LocationNameProvider
) {

    @Provides
    @Reusable
    fun provideLocationNameProvider(): LocationNameProvider = locationNameProvider
}
