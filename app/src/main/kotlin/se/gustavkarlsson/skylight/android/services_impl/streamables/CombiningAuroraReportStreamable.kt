package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function5
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Streamable
import timber.log.Timber

class CombiningAuroraReportStreamable(
	now: Single<Instant>,
	locationNames: Flowable<Optional<String>>,
	kpIndexes: Flowable<KpIndex>,
	geomagLocations: Flowable<GeomagLocation>,
	darknesses: Flowable<Darkness>,
	visibilities: Flowable<Weather>
) : Streamable<AuroraReport> {

	override val stream: Flowable<AuroraReport> =
		Flowable.combineLatest(
			locationNames,
			kpIndexes,
			geomagLocations,
			darknesses,
			visibilities,
			Function5 { locationName: Optional<String>,
						kpIndex: KpIndex,
						geomagLocation: GeomagLocation,
						darkness: Darkness,
						weather: Weather ->
				val factors = AuroraFactors(kpIndex, geomagLocation, darkness, weather)
				AuroraReport(now.blockingGet(), locationName.orNull(), factors)
			})
			.doOnNext { Timber.i("Streamed aurora report: %s", it) }
}
