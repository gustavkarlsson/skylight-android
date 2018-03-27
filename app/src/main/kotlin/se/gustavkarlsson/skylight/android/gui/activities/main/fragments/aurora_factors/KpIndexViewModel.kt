package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class KpIndexViewModel(
	factors: Flowable<KpIndex>,
	formatter: SingleValueFormatter<KpIndex>,
	chanceEvaluator: ChanceEvaluator<KpIndex>
) : FactorViewModel<KpIndex>(factors, formatter, chanceEvaluator)
