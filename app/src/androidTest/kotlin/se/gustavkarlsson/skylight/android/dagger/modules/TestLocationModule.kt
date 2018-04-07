package se.gustavkarlsson.skylight.android.dagger.modules

import com.hadisatrio.optional.Optional
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.test.TestLocationProvider
import java.util.concurrent.TimeUnit

@Module
class TestLocationModule {

	@Provides
	@Reusable
	fun provideTestLocationProvider(): TestLocationProvider =
		TestLocationProvider({ Location(0.0, 0.0) })

	@Provides
	@Reusable
	fun provideLocationProvider(testLocationProvider: TestLocationProvider): LocationProvider =
		testLocationProvider

	@Provides
	@Reusable
	fun provideLocationStreamable(
		locationProvider: LocationProvider
	): Streamable<Location> = object : Streamable<Location> {
		override val stream: Flowable<Location>
			get() = locationProvider.get()
				.repeatWhen { it.delay(POLLING_INTERVAL.toMillis(), TimeUnit.MILLISECONDS) }
				.filter(Optional<Location>::isPresent)
				.map(Optional<Location>::get)

	}

	@Provides
	@Reusable
	fun provideLocationFlowable(
		streamable: Streamable<Location>
	): Flowable<Location> = streamable.stream
		.replay(1)
		.refCount()

	companion object {
		private val POLLING_INTERVAL = Duration.ofMinutes(15)
	}
}
