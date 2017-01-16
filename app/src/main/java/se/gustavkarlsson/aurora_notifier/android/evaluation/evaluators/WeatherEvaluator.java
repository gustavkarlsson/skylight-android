package se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators;

import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.evaluation.Evaluator;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.UNKNOWN;

public class WeatherEvaluator implements Evaluator {
	private final RealmWeather weather;

	public WeatherEvaluator(RealmWeather weather) {
		this.weather = weather;
	}

	@Override
	public AuroraEvaluation evaluate() {
		AuroraEvaluation evaluation = new AuroraEvaluation();
		Integer cloudPercentage = weather.getCloudPercentage();

		if (cloudPercentage == null) {
			evaluation.updateChance(UNKNOWN);
			evaluation.addComplication("Could not get the current weather");
			return evaluation;
		}
		if (cloudPercentage > 50) {
			evaluation.updateChance(NONE);
			evaluation.addComplication("It's too cloudy");
		} else if (cloudPercentage > 25) {
			evaluation.updateChance(LOW);
			evaluation.addComplication("Clouds might obstruct the view");
		}
		return evaluation;
	}
}
