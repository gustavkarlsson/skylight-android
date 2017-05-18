package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.HIGH;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.MEDIUM;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.UNKNOWN;

public class SunPositionEvaluator {
	public AuroraChance evaluate(SunPosition sunPosition) {
		Float zenithAngle = sunPosition.getZenithAngle();
		if (zenithAngle == null) {
			return UNKNOWN;
		}
		if (zenithAngle < 96) {
			return NONE;
		} else if (zenithAngle < 102) {
			return LOW;
		} else if (zenithAngle < 108) {
			return MEDIUM;
		} else {
			return HIGH;
		}
	}
}
