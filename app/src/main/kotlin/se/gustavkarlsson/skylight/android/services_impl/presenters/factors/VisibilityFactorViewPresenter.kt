package se.gustavkarlsson.skylight.android.services_impl.presenters.factors

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter

class VisibilityFactorViewPresenter(
	factorView: AuroraFactorView,
	chanceEvaluator: ChanceEvaluator<Visibility>,
	colorConverter: ChanceToColorConverter
) : AbstractAuroraFactorViewPresenter<Visibility>(factorView, chanceEvaluator, colorConverter) {

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
