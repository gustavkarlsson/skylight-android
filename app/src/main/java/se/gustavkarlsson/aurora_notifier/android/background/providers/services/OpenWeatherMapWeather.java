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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OpenWeatherMapWeather that = (OpenWeatherMapWeather) o;

		return cloudPercentage != null ? cloudPercentage.equals(that.cloudPercentage) : that.cloudPercentage == null;

	}

	@Override
	public int hashCode() {
		return cloudPercentage != null ? cloudPercentage.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "OpenWeatherMapWeather{" +
				"cloudPercentage='" + cloudPercentage + '\'' +
				'}';
	}
}
