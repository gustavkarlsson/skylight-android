package se.gustavkarlsson.skylight.android.services_impl.presenters.factors

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import java.util.*

class GeomagLocationFactorViewPresenter(factorView: AuroraFactorView, chanceEvaluator: ChanceEvaluator<GeomagLocation>, colorConverter: ChanceToColorConverter) : AbstractAuroraFactorViewPresenter<GeomagLocation>(factorView, chanceEvaluator, colorConverter) {

	override val fullTitleResourceId: Int
        get() = R.string.factor_geomag_location_title_full

	override val descriptionResourceId: Int
        get() = R.string.factor_geomag_location_desc

	override fun evaluateText(factor: GeomagLocation): String {
        val latitude = factor.latitude ?: return "?"
        return String.format(Locale.ENGLISH, "%.0f°", latitude)
    }

}
