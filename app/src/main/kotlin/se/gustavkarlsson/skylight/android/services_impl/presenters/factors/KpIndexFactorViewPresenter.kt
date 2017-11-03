package se.gustavkarlsson.skylight.android.services_impl.presenters.factors

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter

class KpIndexFactorViewPresenter(
	factorView: AuroraFactorView,
	chanceEvaluator: ChanceEvaluator<KpIndex>,
	colorConverter: ChanceToColorConverter
) : AbstractAuroraFactorViewPresenter<KpIndex>(factorView, chanceEvaluator, colorConverter) {

	override val fullTitleResourceId: Int
        get() = R.string.factor_kp_index_title_full

    override val descriptionResourceId: Int
        get() = R.string.factor_kp_index_desc

    override fun evaluateText(factor: KpIndex): String {
        val kpIndex = factor.value ?: return "?"
        val whole = kpIndex.toInt()
        val part = kpIndex - whole
        val partString = parsePart(part)
        val wholeString = parseWhole(whole, partString)
        return wholeString + partString
    }

    private fun parsePart(part: Double): String {
        if (part < 0.15) {
            return ""
        }
        if (part < 0.5) {
            return "+"
        }
        return "-"
    }

    private fun parseWhole(whole: Int, partString: String): String {
        val wholeAdjusted = if (partString == "-") whole + 1 else whole
        return Integer.toString(wholeAdjusted)
    }
}
