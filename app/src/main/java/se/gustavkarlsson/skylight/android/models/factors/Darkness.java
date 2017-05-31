package se.gustavkarlsson.skylight.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class Darkness {
	Float sunZenithAngle;

	public Darkness() {
	}

	public Darkness(Float sunZenithAngle) {
		this.sunZenithAngle = sunZenithAngle;
	}

	public Float getSunZenithAngle() {
		return sunZenithAngle;
	}

	@Override
	public String toString() {
		return "Darkness{" +
				"sunZenithAngle=" + sunZenithAngle +
				'}';
	}
}
