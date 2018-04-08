package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import android.location.Geocoder
import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeocoderLocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.LocationNameProviderStreamable

class GeocoderLocationNameModule(context: Context, locationFlowable: Flowable<Location>) :
	LocationNameModule {

	override val locationNameProvider: LocationNameProvider by lazy {
		val geocoder = Geocoder(context)
		GeocoderLocationNameProvider(geocoder)
	}

	override val locationNameStreamable: Streamable<Optional<String>> by lazy {
		LocationNameProviderStreamable(locationFlowable, locationNameProvider, RETRY_DELAY)
	}

	override val locationNameFlowable: Flowable<Optional<String>> by lazy {
		locationNameStreamable.stream
			.replay(1)
			.refCount()
	}

	// TODO Make configurable in constructor?
	companion object {
		private val RETRY_DELAY = Duration.ofSeconds(10)
	}
}
