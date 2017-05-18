package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class GeomagLocation {
	Float degreesFromClosestPole;

	GeomagLocation() {
	}

	public GeomagLocation(Float degreesFromClosestPole) {
		this.degreesFromClosestPole = degreesFromClosestPole;
	}

	public Float getDegreesFromClosestPole() {
		return degreesFromClosestPole;
	}

	@Override
	public String toString() {
		return "GeomagLocation{" +
				"degreesFromClosestPole=" + degreesFromClosestPole +
				'}';
	}
}
