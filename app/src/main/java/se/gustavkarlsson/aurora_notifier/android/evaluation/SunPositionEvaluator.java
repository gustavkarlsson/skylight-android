package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;

public class SunPositionEvaluator implements ChanceEvaluator<SunPosition> {
	public Chance evaluate(SunPosition sunPosition) {
		Float zenithAngle = sunPosition.getZenithAngle();
		if (zenithAngle == null) {
			return Chance.unknown();
		}
		return Chance.of((1.0 / 12.0) * zenithAngle - 8.0); // 96-108
	}
}
