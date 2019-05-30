package se.gustavkarlsson.skylight.android.lib.geomaglocation

import org.koin.dsl.module.module

val geomagLocationModule = module {

	single<GeomagLocationProvider> {
		GeomagLocationProviderImpl(time = get())
	}
}
