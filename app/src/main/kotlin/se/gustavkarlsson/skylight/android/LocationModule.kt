package se.gustavkarlsson.skylight.android

import android.Manifest
import io.reactivex.Flowable
import org.koin.dsl.module.module
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.ReactiveLocationLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.ReactiveLocationProviderStreamable

val locationModule = module {

	single {
		ReactiveLocationProvider(get())
	}

	single<LocationProvider> {
		ReactiveLocationLocationProvider(get())
	}

	single<Streamable<Location>>("location") {
		ReactiveLocationProviderStreamable(get())
	}

	single<Flowable<Location>>("location") {
		get<Streamable<Location>>("location")
			.stream
			.replay(1)
			.refCount()
	}

	single("locationPermission") {
		Manifest.permission.ACCESS_COARSE_LOCATION
	}

}
