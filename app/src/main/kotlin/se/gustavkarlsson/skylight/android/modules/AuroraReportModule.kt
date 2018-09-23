package se.gustavkarlsson.skylight.android.modules

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.*
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
