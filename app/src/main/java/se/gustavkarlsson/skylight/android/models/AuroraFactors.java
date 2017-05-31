package se.gustavkarlsson.skylight.android.models;

import org.parceler.Parcel;

import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

@Parcel
public class AuroraFactors {
	GeomagActivity geomagActivity;
	GeomagLocation geomagLocation;
	Darkness darkness;
	Visibility visibility;

	AuroraFactors() {
	}

	public AuroraFactors(
			GeomagActivity geomagActivity,
			GeomagLocation geomagLocation,
			Darkness darkness,
			Visibility visibility) {
		this.geomagActivity = geomagActivity;
		this.geomagLocation = geomagLocation;
		this.darkness = darkness;
		this.visibility = visibility;
	}

	public GeomagActivity getGeomagActivity() {
		return geomagActivity;
	}

	public GeomagLocation getGeomagLocation() {
		return geomagLocation;
	}

	public Darkness getDarkness() {
		return darkness;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	@Override
	public String toString() {
		return "AuroraFactors{" +
				"geomagActivity=" + geomagActivity +
				", geomagLocation=" + geomagLocation +
				", darkness=" + darkness +
				", visibility=" + visibility +
				'}';
	}
}
