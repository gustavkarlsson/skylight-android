package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.functions.Function5
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Streamable
import timber.log.Timber

class CombiningAuroraReportStreamable(
	locationNames: Flowable<Optional<String>>,
	kpIndexes: Flowable<Report<KpIndex>>,
	geomagLocations: Flowable<Report<GeomagLocation>>,
	darknesses: Flowable<Report<Darkness>>,
	weathers: Flowable<Report<Weather>>
) : Streamable<AuroraReport> {

	override val stream: Flowable<AuroraReport> =
		Flowable.combineLatest(
			locationNames,
			kpIndexes,
			geomagLocations,
			darknesses,
			weathers,
			Function5 { locationName: Optional<String>,
						kpIndex: Report<KpIndex>,
						geomagLocation: Report<GeomagLocation>,
						darkness: Report<Darkness>,
						weather: Report<Weather> ->
				AuroraReport(locationName.orNull(), kpIndex, geomagLocation, darkness, weather)
			})
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed aurora report: %s", it) }
}
