package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class GeomagLocation {
	float degreesFromClosestPole;

	GeomagLocation() {
	}

	public GeomagLocation(float degreesFromClosestPole) {
		this.degreesFromClosestPole = degreesFromClosestPole;
	}

	public float getDegreesFromClosestPole() {
		return degreesFromClosestPole;
	}

	@Override
	public String toString() {
		return "GeomagLocation{" +
				"degreesFromClosestPole=" + degreesFromClosestPole +
				'}';
	}
}
