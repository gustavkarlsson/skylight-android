package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.HIGH;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.MEDIUM;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;

public class GeomagActivityEvaluator {
	public AuroraChance evaluate(GeomagActivity geomagActivity) {
		float kpIndex = geomagActivity.getKpIndex();
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
