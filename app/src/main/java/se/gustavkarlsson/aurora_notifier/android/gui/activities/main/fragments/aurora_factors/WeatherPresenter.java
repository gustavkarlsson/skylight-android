package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.WeatherEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

class WeatherPresenter extends AbstractAuroraFactorPresenter {
	private final WeatherEvaluator evaluator = new WeatherEvaluator();

	WeatherPresenter(AuroraFactorView factorView) {
		super(factorView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_weather_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_weather_desc;
	}

	void onUpdate(Weather weather) {
		setBackgroundColor(evaluator.evaluate(weather));
		setFactorValue(evaluateText(weather));
	}

	private static String evaluateText(Weather weather) {
		Integer clouds = weather.getCloudPercentage();
		if (clouds == null) {
			return "?";
		}
		return Integer.toString(clouds) + '%';
	}
}
