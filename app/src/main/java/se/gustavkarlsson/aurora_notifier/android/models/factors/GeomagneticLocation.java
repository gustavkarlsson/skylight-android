package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class GeomagneticLocation {
	float degreesFromCLosestPole;

	GeomagneticLocation() {
	}

	public GeomagneticLocation(float degreesFromCLosestPole) {
		this.degreesFromCLosestPole = degreesFromCLosestPole;
	}

	public float getDegreesFromClosestPole() {
		return degreesFromCLosestPole;
	}

	@Override
	public String toString() {
		return "GeomagneticLocation{" +
				"degreesFromCLosestPole=" + degreesFromCLosestPole +
				'}';
	}
}
