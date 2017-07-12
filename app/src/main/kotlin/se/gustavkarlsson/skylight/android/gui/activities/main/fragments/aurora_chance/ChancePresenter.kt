package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.AuroraReport

class ChancePresenter(
		private val chanceTextView: TextView,
		private val evaluator: ChanceEvaluator<AuroraReport>
) {

    fun present(report: AuroraReport) {
        val chance = evaluator.evaluate(report)
        val chanceLevel = ChanceLevel.fromChance(chance)
		async(UI) {
			chanceTextView.setText(chanceLevel.resourceId)
		}
    }
}
