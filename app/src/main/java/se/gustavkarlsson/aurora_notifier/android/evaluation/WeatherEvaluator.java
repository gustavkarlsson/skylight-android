package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.HIGH;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.MEDIUM;
import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.NONE;

public class WeatherEvaluator {
	public AuroraChance evaluate(Weather weather) {
		int clouds = weather.getCloudPercentage();
		if (clouds < 10) {
			return HIGH;
		} else if (clouds < 30) {
			return MEDIUM;
		} else if (clouds < 50) {
			return LOW;
		} else {
			return NONE;
		}
	}
}
