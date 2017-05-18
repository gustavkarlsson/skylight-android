package se.gustavkarlsson.aurora_notifier.android.models;

import org.parceler.Parcel;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

@Parcel
public class AuroraFactors {
	GeomagActivity geomagActivity;
	GeomagLocation geomagLocation;
	SunPosition sunPosition;
	Weather weather;

	AuroraFactors() {
	}

	public AuroraFactors(
			GeomagActivity geomagActivity,
			GeomagLocation geomagLocation,
			SunPosition sunPosition,
			Weather weather) {
		this.geomagActivity = geomagActivity;
		this.geomagLocation = geomagLocation;
		this.sunPosition = sunPosition;
		this.weather = weather;
	}

	public GeomagActivity getGeomagActivity() {
		return geomagActivity;
	}

	public GeomagLocation getGeomagLocation() {
		return geomagLocation;
	}

	public SunPosition getSunPosition() {
		return sunPosition;
	}

	public Weather getWeather() {
		return weather;
	}

	@Override
	public String toString() {
		return "AuroraFactors{" +
				"geomagActivity=" + geomagActivity +
				", geomagLocation=" + geomagLocation +
				", sunPosition=" + sunPosition +
				", weather=" + weather +
				'}';
	}
}
