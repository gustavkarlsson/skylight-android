package se.gustavkarlsson.aurora_notifier.android.providers.services;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import se.gustavkarlsson.aurora_notifier.android.providers.Weather;

@Root(strict = false, name = "current")
public class OpenWeatherMapWeather implements Weather {
	@Path("clouds")
	@Attribute(name = "value")
	private String cloudiness;

	@Override
	public String getCloudiness() {
		return cloudiness;
	}
}
