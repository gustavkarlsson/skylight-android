package se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators;

import java.util.LinkedList;
import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;

import static se.gustavkarlsson.aurora_notifier.android.models.AuroraChance.NONE;

public class SolarActivityAndGeomagneticLocationComplicationEvaluator implements ComplicationEvaluator {
	private final SolarActivity solarActivity;
	private final GeomagneticLocation geomagneticLocation;

	public SolarActivityAndGeomagneticLocationComplicationEvaluator(SolarActivity solarActivity, GeomagneticLocation geomagneticLocation) {
		this.solarActivity = solarActivity;
		this.geomagneticLocation = geomagneticLocation;
	}

	@Override
	public List<AuroraComplication> evaluate() {
		List<AuroraComplication> complications = new LinkedList<>();
		float kpIndex = this.solarActivity.getKpIndex();
		float degreesFromClosestPole = geomagneticLocation.getDegreesFromClosestPole();

		if (kpIndex < 1) {
			complications.add(new AuroraComplication(
					NONE,
					R.string.complication_weak_solar_activity_title,
					R.string.complication_weak_solar_activity_desc));
		}
		if (degreesFromClosestPole > 35) {
			complications.add(new AuroraComplication(
					NONE,
					R.string.complication_bad_location_title,
					R.string.complication_bad_location_desc));
		}
		return complications;
		// TODO fill out the rest
	}
}
