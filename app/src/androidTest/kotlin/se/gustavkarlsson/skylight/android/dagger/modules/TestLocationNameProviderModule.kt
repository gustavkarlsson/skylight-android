package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.test.TestLocationNameProvider

@Module
class TestLocationNameProviderModule {

    @Provides
    @Reusable
    fun provideLocationNameProvider(testLocationNameProvider: TestLocationNameProvider): LocationNameProvider = testLocationNameProvider

	@Provides
	@Reusable
	fun provideTestLocationProvider(): TestLocationNameProvider = TestLocationNameProvider({ "Garden of Eden" })
}
