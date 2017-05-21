package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerEvaluationComponent;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

class GeomagLocationPresenter extends AbstractAuroraFactorPresenter<GeomagLocation> {

	GeomagLocationPresenter(AuroraFactorView factorView) {
		super(factorView, DaggerEvaluationComponent.create().getGeomagLocationEvaluator());
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
