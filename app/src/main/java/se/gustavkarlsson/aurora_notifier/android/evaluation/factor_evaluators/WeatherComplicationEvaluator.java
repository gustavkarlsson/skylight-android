package se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators;

import java.util.LinkedList;
import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

import static se.gustavkarlsson.aurora_notifier.android.models.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.models.AuroraChance.NONE;

public class WeatherComplicationEvaluator implements ComplicationEvaluator {
	private final Weather weather;

	public WeatherComplicationEvaluator(Weather weather) {
		this.weather = weather;
	}

	@Override
	public List<AuroraComplication> evaluate() {
		List<AuroraComplication> complications = new LinkedList<>();
		Integer cloudPercentage = weather.getCloudPercentage();
		if (cloudPercentage > 50) {
			complications.add(new AuroraComplication(
					NONE,
					R.string.complication_too_cloudy_title,
					R.string.complication_too_cloudy_desc));
		} else if (cloudPercentage > 25) {
			complications.add(new AuroraComplication(
					LOW,
					R.string.complication_cloudy_title,
					R.string.complication_cloudy_desc));
		}
		return complications;
	}
}
