package se.gustavkarlsson.skylight.android.di.modules

import android.location.Geocoder
import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeocoderLocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.LocationNameProviderStreamable

class GeocoderLocationNameModule(
	contextModule: ContextModule,
	locationModule: LocationModule
) : LocationNameModule {

	override val locationNameProvider: LocationNameProvider by lazy {
		val geocoder = Geocoder(contextModule.context)
		GeocoderLocationNameProvider(geocoder)
	}

	override val locationNameStreamable: Streamable<Optional<String>> by lazy {
		LocationNameProviderStreamable(
			locationModule.locationFlowable,
			locationNameProvider,
			RETRY_DELAY
		)
	}

	override val locationNameFlowable: Flowable<Optional<String>> by lazy {
		locationNameStreamable.stream
			.replay(1)
			.refCount()
	}

	// TODO Make configurable in constructor?
	companion object {
		private val RETRY_DELAY = 10.seconds
	}
}
