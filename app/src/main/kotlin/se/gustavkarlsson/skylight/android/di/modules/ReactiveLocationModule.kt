package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import io.reactivex.Flowable
import org.threeten.bp.Duration
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.ReactiveLocationLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.ReactiveLocationProviderStreamable

class ReactiveLocationModule(context: Context) : LocationModule {

	private val reactiveLocationProvider: ReactiveLocationProvider by lazy {
		ReactiveLocationProvider(context)
	}

	override val locationProvider: LocationProvider by lazy {
		ReactiveLocationLocationProvider(reactiveLocationProvider)
	}
	override val locationStreamable: Streamable<Location> by lazy {
		ReactiveLocationProviderStreamable(reactiveLocationProvider, POLLING_INTERVAL, RETRY_DELAY)
	}

	override val locationFlowable: Flowable<Location> by lazy {
		locationStreamable.stream
			.replay(1)
			.refCount()
	}

	companion object {
		private val POLLING_INTERVAL = Duration.ofMinutes(15)
		private val RETRY_DELAY = Duration.ofMinutes(1)
	}
}
