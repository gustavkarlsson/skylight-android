package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class Weather {
	Integer cloudPercentage;

	Weather() {
	}

	public Weather(Integer cloudPercentage) {
		this.cloudPercentage = cloudPercentage;
	}

	public Integer getCloudPercentage() {
		return cloudPercentage;
	}

	@Override
	public String toString() {
		return "Weather{" +
				"cloudPercentage=" + cloudPercentage +
				'}';
	}
}
