package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import java.util.Locale;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;

public class GeomagLocationPresenter extends AbstractAuroraFactorPresenter<GeomagLocation> {

	public GeomagLocationPresenter(AuroraFactorView factorView, ChanceEvaluator<GeomagLocation> chanceEvaluator, ChanceToColorConverter colorConverter) {
		super(factorView, chanceEvaluator, colorConverter);
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
