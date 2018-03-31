package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.KlausBrunnerDarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.DarknessProviderStreamable

@Module
class DarknessModule {

	@Provides
	@Reusable
	fun provideDarknessProvider(): DarknessProvider = KlausBrunnerDarknessProvider()

	@Provides
	@Reusable
	fun provideDarknessStreamable(
		locations: Flowable<Location>,
		provider: DarknessProvider,
		now: Single<Instant>
	): Streamable<Darkness> =
		DarknessProviderStreamable(locations, provider, now, DARKNESS_POLLING_INTERVAL, RETRY_DELAY)

	@Provides
	@Reusable
	fun provideDarknessFlowable(
		streamable: Streamable<Darkness>
	): Flowable<Darkness> = streamable.stream
		.replay(1)
		.refCount()

	companion object {
	    private val DARKNESS_POLLING_INTERVAL = Duration.ofMinutes(1)
		private val RETRY_DELAY = Duration.ofSeconds(5)
	}
}
