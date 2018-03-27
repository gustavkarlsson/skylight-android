package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class DarknessViewModel(
	factors: Flowable<Darkness>,
	formatter: SingleValueFormatter<Darkness>,
	chanceEvaluator: ChanceEvaluator<Darkness>
) : FactorViewModel<Darkness>(factors, formatter, chanceEvaluator)
