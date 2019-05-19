package se.gustavkarlsson.skylight.android.lib.geomaglocation

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider

val geomagLocationModule = module {

	single<GeomagLocationProvider> {
		GeomagLocationProviderImpl(time = get())
	}
}