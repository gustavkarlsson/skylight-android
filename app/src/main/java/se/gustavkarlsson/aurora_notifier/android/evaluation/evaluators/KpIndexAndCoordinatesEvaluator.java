package se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.evaluation.Evaluator;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;

public class KpIndexAndCoordinatesEvaluator implements Evaluator {
	private final RealmKpIndex kpIndex;
	private final RealmGeomagneticCoordinates geomagneticCoordinates;

	public KpIndexAndCoordinatesEvaluator(RealmKpIndex kpIndex, RealmGeomagneticCoordinates geomagneticCoordinates) {
		this.kpIndex = kpIndex;
		this.geomagneticCoordinates = geomagneticCoordinates;
	}

	@Override
	public AuroraEvaluation evaluate() {
		AuroraEvaluation evaluation = new AuroraEvaluation();
		Float kpIndex = this.kpIndex.getKpIndex();
		Float degreesFromClosestPole = geomagneticCoordinates.getDegreesFromClosestPole();

		if (kpIndex == null) {
			evaluation.updateChance(AuroraChance.UNKNOWN);
			evaluation.addComplication(
					R.string.complication_unable_to_get_solar_activity_title,
					R.string.complication_unable_to_get_solar_activity_desc);
		}
		if (degreesFromClosestPole == null) {
			evaluation.updateChance(AuroraChance.UNKNOWN);
			evaluation.addComplication(
					R.string.complication_unable_to_get_location_title,
					R.string.complication_unable_to_get_location_desc);
		}
		if (kpIndex != null && kpIndex < 1) {
			evaluation.updateChance(NONE);
			evaluation.addComplication(
					R.string.complication_not_enough_solar_activity_title,
					R.string.complication_not_enough_solar_activity_desc);
		}
		if (degreesFromClosestPole != null && degreesFromClosestPole > 35) {
			evaluation.updateChance(NONE);
			evaluation.addComplication(
					R.string.complication_too_far_away_title,
					R.string.complication_too_far_away_desc);
		}
		if (kpIndex == null || degreesFromClosestPole == null) {
			return evaluation;
		}
		return evaluation;
		// TODO fill out the rest
	}
}
