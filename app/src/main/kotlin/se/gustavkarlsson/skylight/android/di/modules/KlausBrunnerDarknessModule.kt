package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.KlausBrunnerDarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.DarknessProviderStreamable

class KlausBrunnerDarknessModule(now: Single<Instant>, locations: Flowable<Location>) : DarknessModule {

	override val darknessProvider: DarknessProvider by lazy { KlausBrunnerDarknessProvider() }

	override val darknessStreamable: Streamable<Darkness> by lazy {
		DarknessProviderStreamable(locations, darknessProvider, now, DARKNESS_POLLING_INTERVAL, RETRY_DELAY)
	}
	override val darknessFlowable: Flowable<Darkness> by lazy {
		darknessStreamable.stream
			.replay(1)
			.refCount()
	}

	// TODO Configure via constructor
	companion object {
	    private val DARKNESS_POLLING_INTERVAL = 1.minutes
		private val RETRY_DELAY = 5.seconds
	}
}
