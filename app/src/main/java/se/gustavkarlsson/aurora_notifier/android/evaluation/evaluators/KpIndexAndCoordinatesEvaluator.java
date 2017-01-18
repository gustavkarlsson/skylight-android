package se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators;

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
			evaluation.addComplication("Unable to get solar activity");
		}
		if (degreesFromClosestPole == null) {
			evaluation.updateChance(AuroraChance.UNKNOWN);
			evaluation.addComplication("Unable to get location");
		}
		if (kpIndex != null && kpIndex < 1) {
			evaluation.updateChance(NONE);
			evaluation.addComplication("Not enough solar activity");
		}
		if (degreesFromClosestPole != null && degreesFromClosestPole > 35) {
			evaluation.updateChance(NONE);
			evaluation.addComplication("Too far away");
		}
		if (kpIndex == null || degreesFromClosestPole == null) {
			return evaluation;
		}
		return evaluation;
		// TODO fill out the rest
	}
}
