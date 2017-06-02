package se.gustavkarlsson.skylight.android.evaluation;

import javax.inject.Inject;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

@Reusable
public class VisibilityEvaluator implements ChanceEvaluator<Visibility> {

	@Inject
	VisibilityEvaluator() {
	}

	public Chance evaluate(Visibility visibility) {
		Integer clouds = visibility.getCloudPercentage();
		if (clouds == null) {
			return Chance.unknown();
		}
		return Chance.of((-1.0 / 50.0) * ((double) clouds) + 1.0);
	}
}
