package se.gustavkarlsson.aurora_notifier.android.background.providers.services;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import se.gustavkarlsson.aurora_notifier.android.background.providers.Weather;

import static java.lang.Integer.*;

@Root(strict = false, name = "current")
public class OpenWeatherMapWeather implements Weather {
	@Path("clouds")
	@Attribute(name = "value")
	private String cloudPercentage;

	@Override
	public int getCloudPercentage() {
		return parseInt(cloudPercentage);
	}
}
