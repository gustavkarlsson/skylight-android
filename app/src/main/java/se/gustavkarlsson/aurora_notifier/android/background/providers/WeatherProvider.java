package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

public interface WeatherProvider {
	Weather getWeather(double latitude, double longitude);
}
