package se.gustavkarlsson.skylight.android.location

import android.Manifest
import com.google.android.gms.location.LocationRequest
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
		ReactiveLocationLocationProvider(
			reactiveLocationProvider = get(),
			timeout = 30.seconds,
			requestAccuracy = get("locationAccuracy"),
			throttleDuration = 1.minutes,
			firstPollingInterval = 10.seconds,
			restPollingInterval = 10.minutes,
			retryDelay = 1.minutes
		)
	}

	single("locationPermission") {
		Manifest.permission.ACCESS_FINE_LOCATION
	}

	single("locationAccuracy") {
		LocationRequest.PRIORITY_HIGH_ACCURACY
	}

}
