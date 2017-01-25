package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java8.util.stream.StreamSupport;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraEvaluation;
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
	public ObservableList<ComplicationViewModel> getComplications() {
		ObservableArrayList<ComplicationViewModel> list = new ObservableArrayList<>();
		StreamSupport.stream(auroraEvaluator.evaluate().getComplications())
				.map(ComplicationViewModel::new)
				.forEach(list::add);
		return list;
	}
}
