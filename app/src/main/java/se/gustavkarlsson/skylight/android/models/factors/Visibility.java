package se.gustavkarlsson.skylight.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class Visibility {
	Integer cloudPercentage;

	public Visibility() {
	}

	public Visibility(Integer cloudPercentage) {
		this.cloudPercentage = cloudPercentage;
	}

	public Integer getCloudPercentage() {
		return cloudPercentage;
	}

	@Override
	public String toString() {
		return "Visibility{" +
				"cloudPercentage=" + cloudPercentage +
				'}';
	}
}
