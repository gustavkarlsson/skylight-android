package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.test.TestLocationProvider

@Module
class TestLocationProviderModule {

    @Provides
    @Reusable
    fun provideLocationProvider(testLocationProvider: TestLocationProvider): LocationProvider = testLocationProvider

	@Provides
	@Reusable
	fun provideTestLocationProvider(): TestLocationProvider = TestLocationProvider({ Location(0.0, 0.0) })
}
