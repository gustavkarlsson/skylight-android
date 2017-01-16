package se.gustavkarlsson.aurora_notifier.android.evaluation;


import java.util.Arrays;
import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators.KpIndexAndCoordinatesEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators.SunPositionEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators.WeatherEvaluator;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;


public class AuroraEvaluator implements Evaluator {
	private final List<Evaluator> evaluators;

	public AuroraEvaluator(RealmWeather weather, RealmSunPosition sunPosition, RealmKpIndex kpIndex, RealmGeomagneticCoordinates geomagneticCoordinates) {
		evaluators = Arrays.asList(
				new WeatherEvaluator(weather),
				new SunPositionEvaluator(sunPosition),
				new KpIndexAndCoordinatesEvaluator(kpIndex, geomagneticCoordinates)
		);
	}

	public AuroraEvaluation evaluate() {
		AuroraEvaluation evaluation = new AuroraEvaluation();
		for (Evaluator evaluator : evaluators) {
			AuroraEvaluation singleEvaluation = evaluator.evaluate();
			evaluation.updateChance(singleEvaluation.getChance());
			for (String complication : singleEvaluation.getComplications()) {
				evaluation.addComplication(complication);
			}
		}
		return evaluation;
	}
}
