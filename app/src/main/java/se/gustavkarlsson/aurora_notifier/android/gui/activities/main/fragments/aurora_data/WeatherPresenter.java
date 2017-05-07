package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_data;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;

class WeatherPresenter extends AbstractAuroraDataPresenter {
	WeatherPresenter(AuroraDataView dataView) {
		super(dataView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.weather_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.weather_desc;
	}

	void onUpdate(Weather weather) {
		setDataValue(Integer.toString(weather.getCloudPercentage()) + '%');
	}
}
