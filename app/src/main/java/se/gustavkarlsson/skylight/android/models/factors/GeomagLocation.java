package se.gustavkarlsson.skylight.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class GeomagLocation {
	Float latitude;

	public GeomagLocation() {
	}

	public GeomagLocation(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return "GeomagLocation{" +
				"latitude=" + latitude +
				'}';
	}
}
