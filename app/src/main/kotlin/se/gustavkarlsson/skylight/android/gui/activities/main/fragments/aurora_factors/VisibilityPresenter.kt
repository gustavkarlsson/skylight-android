package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.entities.Visibility

class VisibilityPresenter(
	factorView: AuroraFactorView,
	chanceEvaluator: ChanceEvaluator<Visibility>,
	colorConverter: ChanceToColorConverter
) : AbstractAuroraFactorPresenter<Visibility>(factorView, chanceEvaluator, colorConverter) {

	override val fullTitleResourceId: Int
        get() = R.string.factor_visibility_title_full

	override val descriptionResourceId: Int
        get() = R.string.factor_visibility_desc

	override fun evaluateText(factor: Visibility): String {
        val clouds = factor.cloudPercentage ?: return "?"
        val visibilityPercentage = 100 - clouds
        return Integer.toString(visibilityPercentage) + '%'
    }
}
