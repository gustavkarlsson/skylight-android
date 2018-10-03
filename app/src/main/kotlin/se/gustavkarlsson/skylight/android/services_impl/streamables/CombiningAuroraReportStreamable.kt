package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
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
		Flowables.combineLatest(
			locationNames,
			kpIndexes,
			geomagLocations,
			darknesses,
			weathers
		) { locationName, kpIndex, geomagLocation, darkness, weather ->
			AuroraReport(locationName.orNull(), kpIndex, geomagLocation, darkness, weather)
		}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed aurora report: %s", it) }
}
