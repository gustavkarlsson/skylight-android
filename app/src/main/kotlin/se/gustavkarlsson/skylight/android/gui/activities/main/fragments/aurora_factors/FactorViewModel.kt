package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

abstract class FactorViewModel<T>(
	factors: Flowable<T>,
	formatter: SingleValueFormatter<T>,
	chanceEvaluator: ChanceEvaluator<T>
) : ViewModel() {

	val value: Flowable<CharSequence> = factors
		.map(formatter::format)
		.distinctUntilChanged()

	val chance: Flowable<Chance> = factors
		.map(chanceEvaluator::evaluate)
		.distinctUntilChanged()
}
