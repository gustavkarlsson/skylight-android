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
	visibilities: Flowable<Visibility>,
	otherAuroraReports: Flowable<AuroraReport>
) : Streamable<AuroraReport> {

	private val otherAuroraReportsShared = otherAuroraReports.publish().autoConnect(5)

	private val mergedLocationNames = otherAuroraReportsShared
		.map { Optional.ofNullable<String>(it.locationName) }
		.mergeWith(locationNames)

	private val mergedKpIndexes = otherAuroraReportsShared
		.map { it.factors.kpIndex }
		.mergeWith(kpIndexes)

	private val mergedGeomagLocations = otherAuroraReportsShared
		.map { it.factors.geomagLocation }
		.mergeWith(geomagLocations)

	private val mergedDarknesses = otherAuroraReportsShared
		.map { it.factors.darkness }
		.mergeWith(darknesses)

	private val mergedVisibilities = otherAuroraReportsShared
		.map { it.factors.visibility }
		.mergeWith(visibilities)

	override val stream: Flowable<AuroraReport> =
		Flowable.combineLatest(
			mergedLocationNames,
			mergedKpIndexes,
			mergedGeomagLocations,
			mergedDarknesses,
			mergedVisibilities,
			Function5 { locationName: Optional<String>,
						kpIndex: KpIndex,
						geomagLocation: GeomagLocation,
						darkness: Darkness,
						visibility: Visibility ->
				val factors = AuroraFactors(kpIndex, geomagLocation, darkness, visibility)
				AuroraReport(now.blockingGet(), locationName.orNull(), factors)
			})
			.doOnNext { Timber.i("Streamed aurora report: %s", it) }
}
