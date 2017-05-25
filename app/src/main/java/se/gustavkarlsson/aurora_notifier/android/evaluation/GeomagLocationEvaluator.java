package se.gustavkarlsson.aurora_notifier.android.evaluation;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

public class GeomagLocationEvaluator implements ChanceEvaluator<GeomagLocation> {

	@Inject
	GeomagLocationEvaluator() {
	}

	public Chance evaluate(GeomagLocation geomagLocation) {
		Float latitude = geomagLocation.getLatitude();
		if (latitude == null) {
			return Chance.unknown();
		}
		double absoluteLatitude = Math.abs(latitude);
		double chance = (1.0 / 12.0) * absoluteLatitude - (55.0 / 12.0); // 55-67
		if (chance > 1.0) {
			chance = 2.0 - chance;
		}
		return Chance.of(chance);
	}
}
