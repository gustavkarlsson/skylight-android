package se.gustavkarlsson.skylight.android.lib.location

import com.google.android.gms.location.LocationRequest
import org.koin.dsl.module.module
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds

val libLocationModule = module {

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
			retryDelay = 30.seconds
		)
	}

	single("locationAccuracy") {
		LocationRequest.PRIORITY_HIGH_ACCURACY
	}

}