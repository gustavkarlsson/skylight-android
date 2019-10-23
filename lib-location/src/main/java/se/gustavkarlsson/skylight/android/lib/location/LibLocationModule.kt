package se.gustavkarlsson.skylight.android.lib.location

import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.LocationProvider

val libLocationModule = module {

	single<LocationProvider> {
		RxLocationLocationProvider(
			fusedLocation = RxLocation(get()).location(),
			timeout = 30.seconds,
			requestAccuracy = LocationRequest.PRIORITY_HIGH_ACCURACY,
			throttleDuration = 1.minutes,
			firstPollingInterval = 10.seconds,
			restPollingInterval = 10.minutes,
			retryDelay = 30.seconds
		)
	}
}
