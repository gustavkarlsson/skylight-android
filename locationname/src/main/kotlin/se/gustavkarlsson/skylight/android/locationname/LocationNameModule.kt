package se.gustavkarlsson.skylight.android.locationname

import android.location.Geocoder
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

val locationNameModule = module {

	single<LocationNameProvider> {
		val geocoder = Geocoder(get())
		GeocoderLocationNameProvider(geocoder = geocoder, retryDelay = 10.seconds)
	}

}
