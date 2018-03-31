package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Flowable
import org.threeten.bp.Duration
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.ReactiveLocationLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.ReactiveLocationProviderStreamable

@Module
class LocationModule {

	@Provides
	@Reusable
	fun provideReactiveLocationProvider(
		context: Context
	): ReactiveLocationProvider = ReactiveLocationProvider(context)

	@Provides
	@Reusable
	fun provideLocationProvider(
		reactiveLocationProvider: ReactiveLocationProvider
	): LocationProvider = ReactiveLocationLocationProvider(reactiveLocationProvider)

	@Provides
	@Reusable
	fun provideLocationStreamable(
		reactiveLocationProvider: ReactiveLocationProvider
	): Streamable<Location> =
		ReactiveLocationProviderStreamable(reactiveLocationProvider, POLLING_INTERVAL, RETRY_DELAY)

	@Provides
	@Reusable
	fun provideLocationFlowable(
		streamable: Streamable<Location>
	): Flowable<Location> = streamable.stream

	companion object {
		private val POLLING_INTERVAL = Duration.ofMinutes(15)
		private val RETRY_DELAY = Duration.ofMinutes(1)
	}
}
