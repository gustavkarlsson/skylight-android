package se.gustavkarlsson.skylight.android.locationname

import android.location.Geocoder
import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

val locationNameModule = module {

	single<LocationNameProvider> {
		val geocoder = Geocoder(get())
		GeocoderLocationNameProvider(geocoder)
	}

	single<Streamable<Optional<String>>>("locationName") {
		LocationNameProviderStreamable(get("location"), get(), 10.seconds)
	}

	single<Flowable<Optional<String>>>("locationName") {
		get<Streamable<Optional<String>>>("locationName")
			.stream
			.replay(1)
			.refCount()
	}

}
