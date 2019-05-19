package se.gustavkarlsson.skylight.android.lib.geocoder

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Geocoder

val geocoderModule = module {

	single<Geocoder> {
		MapboxGeocoder(BuildConfig.MAPBOX_API_KEY)
	}
}
