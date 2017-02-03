package se.gustavkarlsson.aurora_notifier.android.models.data;

import org.parceler.Parcel;

@Parcel
public class Weather {
	int cloudPercentage;

	Weather() {
	}

	public Weather(int cloudPercentage) {
		this.cloudPercentage = cloudPercentage;
	}

	public int getCloudPercentage() {
		return cloudPercentage;
	}

	@Override
	public String toString() {
		return "Weather{" +
				"cloudPercentage=" + cloudPercentage +
				'}';
	}
}
