package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;

class WeatherPresenter extends AbstractAuroraDataPresenter {
	WeatherPresenter(AuroraDataView dataView) {
		super(dataView);
	}

	void update(Weather weather) {
		setDataValue(Integer.toString(weather.getCloudPercentage()) + '%');
	}
}
