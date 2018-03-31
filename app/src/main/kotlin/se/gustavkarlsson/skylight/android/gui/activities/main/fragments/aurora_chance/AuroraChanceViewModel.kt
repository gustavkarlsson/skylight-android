package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import java.util.concurrent.TimeUnit

class AuroraChanceViewModel(
	auroraReports: Flowable<AuroraReport>,
	auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
	relativeTimeFormatter: RelativeTimeFormatter,
	now: Single<Instant>,
	nowTextThreshold: Duration
) : ViewModel() {

	private val timestamps = auroraReports
		.map(AuroraReport::timestamp)
		.distinctUntilChanged()
		.replay(1)
		.refCount()

	val chanceLevel: Flowable<Int> = auroraReports
		.map(auroraChanceEvaluator::evaluate)
		.map(ChanceLevel.Companion::fromChance)
		.map(ChanceLevel::resourceId)
		.distinctUntilChanged()

	val timeSinceUpdate: Flowable<CharSequence> = timestamps
		.switchMap {
			Flowable.just(it)
				.repeatWhen { it.delay(1, TimeUnit.SECONDS) }
		}
		.map {
			relativeTimeFormatter.format(it, now.blockingGet(), nowTextThreshold)
		}
		.distinctUntilChanged()

	val timeSinceUpdateVisibility: Flowable<Boolean> = timestamps
		.map {
			when {
				it <= Instant.EPOCH -> false
				else -> true
			}
		}
		.distinctUntilChanged()
}
