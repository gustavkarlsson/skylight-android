package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraEvaluationViewModel extends BaseObservable {
	private AuroraEvaluation evaluation;

	public AuroraEvaluationViewModel(AuroraEvaluation evaluation) {
		this.evaluation = evaluation;
	}

	public void update(AuroraEvaluation evaluation) {
		this.evaluation = evaluation;
		notifyChange();
	}

	@Bindable
	public AuroraEvaluation getEvaluation() {
		return evaluation;
	}
}
