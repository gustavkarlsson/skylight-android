package se.gustavkarlsson.aurora_notifier.android.services;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public interface WeatherService {

	Timestamped<Weather> getWeather(double latitude, double longitude) throws ServiceException;
}
