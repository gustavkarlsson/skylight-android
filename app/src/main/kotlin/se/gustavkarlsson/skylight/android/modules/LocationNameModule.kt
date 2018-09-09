package se.gustavkarlsson.skylight.android.modules

import android.location.Geocoder
import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeocoderLocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.LocationNameProviderStreamable

val locationNameModule = module {

	single<LocationNameProvider> {
		val geocoder = Geocoder(get())
		GeocoderLocationNameProvider(geocoder)
	}

	single<Streamable<Optional<String>>>("locationName") {
		LocationNameProviderStreamable(get("location"), get())
	}

	single<Flowable<Optional<String>>>("locationName") {
		get<Streamable<Optional<String>>>("locationName")
			.stream
			.replay(1)
			.refCount()
	}

}
