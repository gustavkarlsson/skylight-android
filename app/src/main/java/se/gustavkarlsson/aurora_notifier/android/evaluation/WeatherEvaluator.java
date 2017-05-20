package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

public class WeatherEvaluator implements ChanceEvaluator<Weather> {
	public Chance evaluate(Weather weather) {
		Integer clouds = weather.getCloudPercentage();
		if (clouds == null) {
			return Chance.unknown();
		}
		return Chance.of((-1.0 / 50.0) * ((double) clouds) + 1.0);
	}
}
