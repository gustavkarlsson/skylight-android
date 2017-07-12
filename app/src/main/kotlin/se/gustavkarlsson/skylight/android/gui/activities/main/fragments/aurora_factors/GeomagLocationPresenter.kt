package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import java.util.*

class GeomagLocationPresenter(factorView: AuroraFactorView, chanceEvaluator: ChanceEvaluator<GeomagLocation>, colorConverter: ChanceToColorConverter) : AbstractAuroraFactorPresenter<GeomagLocation>(factorView, chanceEvaluator, colorConverter) {

	override val fullTitleResourceId: Int
        get() = R.string.factor_geomag_location_title_full

	override val descriptionResourceId: Int
        get() = R.string.factor_geomag_location_desc

	override fun evaluateText(factor: GeomagLocation): String {
        val latitude = factor.latitude ?: return "?"
        return String.format(Locale.ENGLISH, "%.0f°", latitude)
    }

}
