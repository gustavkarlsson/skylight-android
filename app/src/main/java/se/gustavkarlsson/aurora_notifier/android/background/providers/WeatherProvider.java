package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public interface WeatherProvider {
	Timestamped<? extends Weather> getWeather(double latitude, double longitude) throws ProviderException;
}
