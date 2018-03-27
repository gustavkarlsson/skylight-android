package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeomagLocationProviderImpl
import se.gustavkarlsson.skylight.android.services_impl.streamables.GeomagLocationStreamable

@Module
class GeomagLocationModule {

    @Provides
    @Reusable
    fun provideGeomagLocationProvider(): GeomagLocationProvider = GeomagLocationProviderImpl()

	@Provides
	@Reusable
	fun provideGeomagLocationStreamable(
		locations: Flowable<Location>,
		provider: GeomagLocationProvider
	): Streamable<GeomagLocation> = GeomagLocationStreamable(locations, provider)

	@Provides
	@Reusable
	fun provideGeomagLocationFlowable(
		streamable: GeomagLocationStreamable
	): Flowable<GeomagLocation> = streamable.stream

}
