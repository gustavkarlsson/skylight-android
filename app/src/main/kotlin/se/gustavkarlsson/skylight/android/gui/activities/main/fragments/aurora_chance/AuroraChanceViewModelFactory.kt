package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Reusable
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.dagger.RIGHT_NOW_THRESHOLD_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@Reusable
class AuroraChanceViewModelFactory
@Inject
constructor(
	private val auroraReports: Flowable<AuroraReport>,
	private val auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val relativeTimeFormatter: RelativeTimeFormatter,
	private val now: Single<Instant>,
	@Named(RIGHT_NOW_THRESHOLD_NAME) private val rightNowThreshold: Duration
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return AuroraChanceViewModel(
			auroraReports,
			auroraChanceEvaluator,
			relativeTimeFormatter,
			now,
			rightNowThreshold
		) as T
	}

	companion object {
		private val CLASS = AuroraChanceViewModel::class.java
	}
}
