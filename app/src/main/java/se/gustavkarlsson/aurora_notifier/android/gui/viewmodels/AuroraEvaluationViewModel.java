package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraEvaluator;

public class AuroraEvaluationViewModel extends BaseObservable {

	private final AuroraEvaluator auroraEvaluator;

	public AuroraEvaluationViewModel(AuroraEvaluator auroraEvaluator) {
		this.auroraEvaluator = auroraEvaluator;
	}

	@Bindable
	public String getChance() {
		AuroraChance chance = auroraEvaluator.evaluate().getChance();
		return "Chance: " + chance.toString();
	}

	@Bindable
	public String getComplications() {
		List<String> complications = auroraEvaluator.evaluate().getComplications();
		return "Reasons:\n    " + TextUtils.join("\n    ", complications);
	}
}
