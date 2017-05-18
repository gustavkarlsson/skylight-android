package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.HIGH;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.MEDIUM;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.UNKNOWN;

public class GeomagLocationEvaluator {
	private static final float IDEAL = 23;

	public AuroraChance evaluate(GeomagLocation geomagLocation) {
		Float degrees = geomagLocation.getDegreesFromClosestPole();
		if (degrees == null) {
			return UNKNOWN;
		}
		float diff = (max(degrees, IDEAL) - min(degrees, IDEAL));
		if (diff < 3) {
			return HIGH;
		} else if (diff < 6) {
			return MEDIUM;
		} else if (diff < 9) {
			return LOW;
		} else {
			return NONE;
		}
	}
}
