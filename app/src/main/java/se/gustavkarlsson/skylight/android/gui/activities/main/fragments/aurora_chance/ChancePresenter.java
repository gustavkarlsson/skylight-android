package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.widget.TextView;

import se.gustavkarlsson.skylight.android.evaluation.Chance;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

public class ChancePresenter {
	private final TextView chanceTextView;
	private final ChanceEvaluator<AuroraReport> evaluator;

	public ChancePresenter(TextView chanceTextView, ChanceEvaluator<AuroraReport> evaluator) {
		this.chanceTextView = chanceTextView;
		this.evaluator = evaluator;
	}

	void update(AuroraReport report) {
		Chance chance = evaluator.evaluate(report);
		ChanceLevel chanceLevel = ChanceLevel.Companion.fromChance(chance);
		chanceTextView.setText(chanceLevel.getResourceId());
	}
}
