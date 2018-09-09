package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.functions.Function5
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import timber.log.Timber

class CombiningAuroraReportStreamable(
	timeProvider: TimeProvider,
	locationNames: Flowable<Optional<String>>,
	kpIndexes: Flowable<KpIndex>,
	geomagLocations: Flowable<GeomagLocation>,
	darknesses: Flowable<Darkness>,
	weathers: Flowable<Weather>
) : Streamable<AuroraReport> {

	override val stream: Flowable<AuroraReport> =
		Flowable.combineLatest(
			locationNames,
			kpIndexes,
			geomagLocations,
			darknesses,
			weathers,
			Function5 { locationName: Optional<String>,
						kpIndex: KpIndex,
						geomagLocation: GeomagLocation,
						darkness: Darkness,
						weather: Weather ->
				val factors = AuroraFactors(kpIndex, geomagLocation, darkness, weather)
				AuroraReport(timeProvider.getTime().blockingGet(), locationName.orNull(), factors)
			})
			.doOnNext { Timber.i("Streamed aurora report: %s", it) }
}
