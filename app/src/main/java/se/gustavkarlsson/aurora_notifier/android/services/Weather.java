package se.gustavkarlsson.aurora_notifier.android.services;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(strict = false, name = "current")
public class Weather {

	@Path("clouds")
	@Attribute(name = "value")
	private String cloudiness;

	public String getCloudiness() {
		return cloudiness;
	}
}
