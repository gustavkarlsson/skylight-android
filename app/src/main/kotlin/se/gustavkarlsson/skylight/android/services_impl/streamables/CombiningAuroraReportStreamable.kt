package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraFactors
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Streamable
import timber.log.Timber

class CombiningAuroraReportStreamable(
	locationNames: Flowable<Optional<String>>,
	factors: Flowable<AuroraFactors>,
	now: Single<Instant>,
	otherAuroraReports: Flowable<AuroraReport>
) : Streamable<AuroraReport> {
	override val stream: Flowable<AuroraReport> =
		Flowable.merge(
			Flowable.combineLatest(locationNames, factors,
				BiFunction<Optional<String>, AuroraFactors, AuroraReport> { locationName, factors ->
					AuroraReport(now.blockingGet(), locationName.orNull(), factors)
				}),
			otherAuroraReports
		)
			.doOnNext { Timber.i("Streamed aurora report: %s", it) }
			.replay(1)
			.refCount()
}
