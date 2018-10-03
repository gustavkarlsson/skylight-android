package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.CombiningAuroraReportProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.CombiningAuroraReportStreamable

val auroraReportModule = module {

	single {
		CombiningAuroraReportProvider(get(), get(), get(), get(), get(), get())
	}

	single<AuroraReportProvider> {
		get<CombiningAuroraReportProvider>()
	}

	single {
		val locationNames = get<Flowable<Optional<String>>>("locationName")
		val kpIndexes = get<Flowable<Report<KpIndex>>>("kpIndex")
		val geomagLocations = get<Flowable<Report<GeomagLocation>>>("geomagLocation")
		val darknesses = get<Flowable<Report<Darkness>>>("darkness")
		val weathers = get<Flowable<Report<Weather>>>("weather")
		CombiningAuroraReportStreamable(
			locationNames,
			kpIndexes,
			geomagLocations,
			darknesses,
			weathers
		)
	}

	single<Streamable<AuroraReport>>("auroraReport") {
		get<CombiningAuroraReportStreamable>()
	}

	single<Flowable<AuroraReport>>("auroraReport") {
		get<Streamable<AuroraReport>>("auroraReport")
			.stream
			.replay(1)
			.refCount()
	}
}
