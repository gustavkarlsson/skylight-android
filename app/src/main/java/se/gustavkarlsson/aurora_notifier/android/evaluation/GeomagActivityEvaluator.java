package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.HIGH;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.MEDIUM;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.UNKNOWN;

public class GeomagActivityEvaluator {
	public AuroraChance evaluate(GeomagActivity geomagActivity) {
		Float kpIndex = geomagActivity.getKpIndex();
		if (kpIndex == null) {
			return UNKNOWN;
		}
		if (kpIndex < 1) {
			return NONE;
		} else if (kpIndex < 3) {
			return LOW;
		} else if (kpIndex < 6) {
			return MEDIUM;
		} else {
			return HIGH;
		}
	}
}
