package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.widget.TextView
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.models.AuroraReport

class ChancePresenter(
		private val chanceTextView: TextView,
		private val evaluator: ChanceEvaluator<AuroraReport>
) {

    fun update(report: AuroraReport) {
        val chance = evaluator.evaluate(report)
        val chanceLevel = ChanceLevel.fromChance(chance)
        chanceTextView.setText(chanceLevel.resourceId)
    }
}
