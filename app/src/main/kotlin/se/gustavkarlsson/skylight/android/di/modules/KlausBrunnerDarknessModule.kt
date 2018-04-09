package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.KlausBrunnerDarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.DarknessProviderStreamable

class KlausBrunnerDarknessModule(
	timeModule: TimeModule,
	locationModule: LocationModule
) : DarknessModule {

	override val darknessProvider: DarknessProvider by lazy { KlausBrunnerDarknessProvider() }

	override val darknessStreamable: Streamable<Darkness> by lazy {
		DarknessProviderStreamable(
			locationModule.locationFlowable,
			darknessProvider,
			timeModule.now,
			DARKNESS_POLLING_INTERVAL,
			RETRY_DELAY
		)
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
