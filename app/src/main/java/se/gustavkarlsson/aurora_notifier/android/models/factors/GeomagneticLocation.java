package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class GeomagneticLocation {
	float degreesFromClosestPole;

	GeomagneticLocation() {
	}

	public GeomagneticLocation(float degreesFromClosestPole) {
		this.degreesFromClosestPole = degreesFromClosestPole;
	}

	public float getDegreesFromClosestPole() {
		return degreesFromClosestPole;
	}

	@Override
	public String toString() {
		return "GeomagneticLocation{" +
				"degreesFromClosestPole=" + degreesFromClosestPole +
				'}';
	}
}
