package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
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

	void update(Weather weather) {
		setDataValue(Integer.toString(weather.getCloudPercentage()) + '%');
	}
}
