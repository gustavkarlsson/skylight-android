package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

public class VisibilityEvaluator implements ChanceEvaluator<Visibility> {
	public Chance evaluate(Visibility visibility) {
		Integer clouds = visibility.getCloudPercentage();
		if (clouds == null) {
			return Chance.unknown();
		}
		return Chance.of((-1.0 / 50.0) * ((double) clouds) + 1.0);
	}
}
