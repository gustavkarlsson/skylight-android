package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import java.util.Locale;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;

import static se.gustavkarlsson.skylight.android.Skylight.getApplicationComponent;

public class GeomagLocationPresenter extends AbstractAuroraFactorPresenter<GeomagLocation> {

	public GeomagLocationPresenter(AuroraFactorView factorView) {
		super(factorView, getApplicationComponent().getGeomagLocationEvaluator());
	}

	@Override
	int getFullTitleResourceId() {
		return R.string.factor_geomag_location_title_full;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_geomag_location_desc;
	}

	@Override
	String evaluateText(GeomagLocation geomagLocation) {
		Float latitude = geomagLocation.getLatitude();
		if (latitude == null) {
			return "?";
		}
		return String.format(Locale.ENGLISH, "%.0f°", latitude);
	}

}
