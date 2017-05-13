package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

class WeatherPresenter extends AbstractAuroraFactorPresenter {
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
		setFactorValue(Integer.toString(weather.getCloudPercentage()) + '%');
	}
}
