package se.gustavkarlsson.aurora_notifier.android.models;

import org.parceler.Parcel;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

@Parcel
public class AuroraFactors {
	GeomagActivity geomagActivity;
	GeomagLocation geomagLocation;
	Darkness darkness;
	Weather weather;

	AuroraFactors() {
	}

	public AuroraFactors(
			GeomagActivity geomagActivity,
			GeomagLocation geomagLocation,
			Darkness darkness,
			Weather weather) {
		this.geomagActivity = geomagActivity;
		this.geomagLocation = geomagLocation;
		this.darkness = darkness;
		this.weather = weather;
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

	public Weather getWeather() {
		return weather;
	}

	@Override
	public String toString() {
		return "AuroraFactors{" +
				"geomagActivity=" + geomagActivity +
				", geomagLocation=" + geomagLocation +
				", darkness=" + darkness +
				", weather=" + weather +
				'}';
	}
}
