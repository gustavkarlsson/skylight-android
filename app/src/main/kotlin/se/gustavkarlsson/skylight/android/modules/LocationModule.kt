package se.gustavkarlsson.skylight.android.modules

import android.Manifest
import io.reactivex.Flowable
import org.koin.dsl.module.module
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.ReactiveLocationLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.ReactiveLocationProviderStreamable

val locationModule = module {

	single {
		ReactiveLocationProvider(get())
	}

	single<LocationProvider> {
		ReactiveLocationLocationProvider(get(), 30.seconds)
	}

	single<Streamable<Location>>("location") {
		ReactiveLocationProviderStreamable(get(), 1.minutes, 10.seconds, 10.minutes, 1.minutes)
	}

	single<Flowable<Location>>("location") {
		get<Streamable<Location>>("location")
			.stream
			.replay(1)
			.refCount()
	}

	single("locationPermission") {
		Manifest.permission.ACCESS_FINE_LOCATION
	}

}
