package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.evaluation.Evaluator;

public class AuroraEvaluationViewModel extends BaseObservable {

	private final Evaluator auroraEvaluator;

	public AuroraEvaluationViewModel(Evaluator evaluator) {
		this.auroraEvaluator = evaluator;
	}

	@Bindable
	public String getChance() {
		AuroraChance chance = auroraEvaluator.evaluate().getChance();
		return chance.toString();
	}

	@Bindable
	public String getComplications() {
		List<String> complications = auroraEvaluator.evaluate().getComplications();
		return TextUtils.join("\n", complications);
	}
}
