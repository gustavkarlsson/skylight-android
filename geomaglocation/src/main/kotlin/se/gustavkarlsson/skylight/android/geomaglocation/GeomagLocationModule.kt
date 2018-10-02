package se.gustavkarlsson.skylight.android.geomaglocation

import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider

val geomagLocationModule = module {

	single<GeomagLocationProvider> {
		GeomagLocationProviderImpl(get())
	}

	single<Streamable<Report<GeomagLocation>>>("geomagLocation") {
		val locations = get<Flowable<Location>>("location")
		GeomagLocationProviderStreamable(locations, get())
	}

	single<Flowable<Report<GeomagLocation>>>("geomagLocation") {
		get<Streamable<Report<GeomagLocation>>>("geomagLocation")
			.stream
			.replay(1)
			.refCount()
	}

}
