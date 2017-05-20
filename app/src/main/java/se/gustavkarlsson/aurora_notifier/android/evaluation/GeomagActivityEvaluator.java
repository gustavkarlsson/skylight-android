package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

public class GeomagActivityEvaluator implements ChanceEvaluator<GeomagActivity> {
	public Chance evaluate(GeomagActivity geomagActivity) {
		Float kpIndex = geomagActivity.getKpIndex();
		if (kpIndex == null) {
			return Chance.unknown();
		}
		return Chance.of((1.0 / 9.0) * kpIndex + 0.0); // 0-9
	}
}
