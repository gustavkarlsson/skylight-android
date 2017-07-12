package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.entities.GeomagActivity

class GeomagActivityPresenter(
	factorView: AuroraFactorView,
	chanceEvaluator: ChanceEvaluator<GeomagActivity>,
	colorConverter: ChanceToColorConverter
) : AbstractAuroraFactorPresenter<GeomagActivity>(factorView, chanceEvaluator, colorConverter) {

	override val fullTitleResourceId: Int
        get() = R.string.factor_geomag_activity_title_full

    override val descriptionResourceId: Int
        get() = R.string.factor_geomag_activity_desc

    override fun evaluateText(factor: GeomagActivity): String {
        val kpIndex = factor.kpIndex ?: return "?"
        val whole = kpIndex.toInt()
        val part = kpIndex - whole
        val partString = parsePart(part)
        val wholeString = parseWhole(whole, partString)
        return wholeString + partString
    }

    private fun parsePart(part: Float): String {
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
