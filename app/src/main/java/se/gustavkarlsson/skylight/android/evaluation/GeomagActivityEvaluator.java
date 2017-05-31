package se.gustavkarlsson.skylight.android.evaluation;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;

public class GeomagActivityEvaluator implements ChanceEvaluator<GeomagActivity> {

	@Inject
	GeomagActivityEvaluator() {
	}

	public Chance evaluate(GeomagActivity geomagActivity) {
		Float kpIndex = geomagActivity.getKpIndex();
		if (kpIndex == null) {
			return Chance.unknown();
		}
		return Chance.of((1.0 / 9.0) * kpIndex + 0.0); // 0-9
	}
}
