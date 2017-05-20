package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

public class DarknessEvaluator implements ChanceEvaluator<Darkness> {
	public Chance evaluate(Darkness darkness) {
		Float zenithAngle = darkness.getSunZenithAngle();
		if (zenithAngle == null) {
			return Chance.unknown();
		}
		return Chance.of((1.0 / 12.0) * zenithAngle - 8.0); // 96-108
	}
}
