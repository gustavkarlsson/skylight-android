package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class VisibilityViewModel(
	factors: Flowable<Visibility>,
	formatter: SingleValueFormatter<Visibility>,
	chanceEvaluator: ChanceEvaluator<Visibility>
) : FactorViewModel<Visibility>(factors, formatter, chanceEvaluator)
