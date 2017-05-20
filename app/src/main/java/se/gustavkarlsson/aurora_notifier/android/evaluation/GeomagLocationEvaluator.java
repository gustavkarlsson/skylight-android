package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

public class GeomagLocationEvaluator implements ChanceEvaluator<GeomagLocation> {
	public Chance evaluate(GeomagLocation geomagLocation) {
		Float degrees = geomagLocation.getDegreesFromClosestPole();
		if (degrees == null) {
			return Chance.unknown();
		}
		double chance = (-1.0 / 12.0) * degrees + (35.0 / 12.0); // 23-35
		if (chance > 1.0) {
			chance = 2.0 - chance;
		}
		return Chance.of(chance);
	}
}
