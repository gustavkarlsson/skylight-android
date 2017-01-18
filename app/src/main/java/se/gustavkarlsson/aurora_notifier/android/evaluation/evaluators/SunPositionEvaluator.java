package se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators;

import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.evaluation.Evaluator;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.UNKNOWN;

public class SunPositionEvaluator implements Evaluator {
	private final RealmSunPosition sunPosition;

	public SunPositionEvaluator(RealmSunPosition sunPosition) {
		this.sunPosition = sunPosition;
	}

	@Override
	public AuroraEvaluation evaluate() {
		AuroraEvaluation evaluation = new AuroraEvaluation();
		Float zenithAngle = sunPosition.getZenithAngle();

		if (zenithAngle == null) {
			evaluation.updateChance(UNKNOWN);
			evaluation.addComplication("Unable to get location");
			return evaluation;
		}
		if (zenithAngle < 90) {
			evaluation.updateChance(NONE);
			evaluation.addComplication("Too bright");
		} else if (zenithAngle < 95) {
			evaluation.updateChance(LOW);
			evaluation.addComplication("Probably too bright");
		}
		return evaluation;
	}
}
