package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class AuroraChanceViewModelFactory(
	private val auroraReports: Flowable<AuroraReport>,
	private val auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val relativeTimeFormatter: RelativeTimeFormatter,
	private val chanceLevelFormatter: SingleValueFormatter<ChanceLevel>,
	private val now: Single<Instant>,
	private val rightNowThreshold: Duration
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return AuroraChanceViewModel(
			auroraReports,
			auroraChanceEvaluator,
			relativeTimeFormatter,
			chanceLevelFormatter,
			now,
			rightNowThreshold
		) as T
	}

	companion object {
		private val CLASS = AuroraChanceViewModel::class.java
	}
}
