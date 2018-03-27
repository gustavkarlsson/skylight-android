package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.location.Geocoder
import com.hadisatrio.optional.Optional
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeocoderLocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.LocationNameStreamable

@Module
class LocationNameProviderModule {

	@Provides
	@Reusable
	fun provideGeocoder(
		context: Context
	): Geocoder = Geocoder(context)

	@Provides
	@Reusable
	fun provideLocationNameProvider(
		geocoder: Geocoder
	): LocationNameProvider = GeocoderLocationNameProvider(geocoder)

	@Provides
	@Reusable
	fun provideLocationNameStreamable(
		locations: Flowable<Location>,
		provider: LocationNameProvider
	): Streamable<Optional<String>> = LocationNameStreamable(locations, provider)

	@Provides
	@Reusable
	fun provideLocationFlowable(
		streamable: Streamable<Optional<String>>
	): Flowable<Optional<String>> = streamable.stream
}
