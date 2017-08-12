package se.gustavkarlsson.skylight.android.services_impl.presenters.factors

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import java.lang.Math.*

class DarknessFactorViewPresenter(
	factorView: AuroraFactorView,
	chanceEvaluator: ChanceEvaluator<Darkness>,
	colorConverter: ChanceToColorConverter
) : AbstractAuroraFactorViewPresenter<Darkness>(factorView, chanceEvaluator, colorConverter) {

    override val fullTitleResourceId: Int
        get() = R.string.factor_darkness_title_full

	override val descriptionResourceId: Int
        get() = R.string.factor_darkness_desc

    /*
	 * 0% at 90°. 100% at 108°
	 */
	override fun evaluateText(factor: Darkness): String {
        val zenithAngle = factor.sunZenithAngle ?: return "?"
        val darknessFactor = 1.0 / 18.0 * zenithAngle - 5.0
        var darknessPercentage = round(darknessFactor * 100.0)
        darknessPercentage = max(0, darknessPercentage)
        darknessPercentage = min(100, darknessPercentage)
        return "$darknessPercentage%"
    }
}
