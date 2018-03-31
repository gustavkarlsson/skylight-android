package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraFactorsStreamable

@Module
class AuroraFactorsModule {

	@Provides
	@Reusable
	fun provideAuroraFactorsProvider(
		kpIndexProvider: KpIndexProvider,
		visibilityProvider: VisibilityProvider,
		darknessProvider: DarknessProvider,
		geomagLocProvider: GeomagLocationProvider
	): AuroraFactorsProvider = CombiningAuroraFactorsProvider(kpIndexProvider, visibilityProvider, darknessProvider, geomagLocProvider)

	@Provides
	@Reusable
	fun provideAuroraFactorsStreamable(
		kpIndexes: Flowable<KpIndex>,
		visibilities: Flowable<Visibility>,
		darknesses: Flowable<Darkness>,
		geomagLocations: Flowable<GeomagLocation>
	): Streamable<AuroraFactors> = CombiningAuroraFactorsStreamable(kpIndexes, visibilities, darknesses, geomagLocations)

	@Provides
	@Reusable
	fun provideAuroraFactorsFlowable(
		streamable: Streamable<AuroraFactors>
	): Flowable<AuroraFactors> = streamable.stream
}
