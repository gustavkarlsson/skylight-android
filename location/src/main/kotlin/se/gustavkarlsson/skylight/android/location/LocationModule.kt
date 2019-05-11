package se.gustavkarlsson.skylight.android.location

import android.Manifest
import org.koin.dsl.module.module
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

val locationModule = module {

	single {
		ReactiveLocationProvider(get())
	}

	single<LocationProvider> {
		ReactiveLocationLocationProvider(get(), 30.seconds, 1.minutes, 10.seconds, 10.minutes, 1.minutes)
	}

	single("locationPermission") {
		Manifest.permission.ACCESS_FINE_LOCATION
	}

}
