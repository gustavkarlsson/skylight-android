package se.gustavkarlsson.aurora_notifier.android.evaluation.evaluators;

import se.gustavkarlsson.aurora_notifier.android.R;
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
			evaluation.addComplication(
					R.string.complication_unable_to_get_weather_title,
					R.string.complication_unable_to_get_weather_desc);
			return evaluation;
		}
		if (cloudPercentage > 50) {
			evaluation.updateChance(NONE);
			evaluation.addComplication(
					R.string.complication_too_cloudy_title,
					R.string.complication_too_cloudy_desc);
		} else if (cloudPercentage > 25) {
			evaluation.updateChance(LOW);
			evaluation.addComplication(
					R.string.complication_probably_too_cloudy_title,
					R.string.complication_probably_too_cloudy_desc);
		}
		return evaluation;
	}
}
