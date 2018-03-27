package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class GeomagLocationViewModel(
	factors: Flowable<GeomagLocation>,
	formatter: SingleValueFormatter<GeomagLocation>,
	chanceEvaluator: ChanceEvaluator<GeomagLocation>
) : FactorViewModel<GeomagLocation>(factors, formatter, chanceEvaluator)
