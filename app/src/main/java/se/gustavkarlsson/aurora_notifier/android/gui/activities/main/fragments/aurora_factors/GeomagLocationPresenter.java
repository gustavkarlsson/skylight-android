package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.GeomagLocationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

class GeomagLocationPresenter extends AbstractAuroraFactorPresenter {
	private final GeomagLocationEvaluator evaluator = new GeomagLocationEvaluator();

	GeomagLocationPresenter(AuroraFactorView factorView) {
		super(factorView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_geomag_location_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_geomag_location_desc;
	}

	void onUpdate(GeomagLocation geomagLocation) {
		setBackgroundColor(evaluator.evaluate(geomagLocation));
		setFactorValue(evaluateText(geomagLocation));
	}

	private static String evaluateText(GeomagLocation geomagLocation) {
		Float degrees = geomagLocation.getDegreesFromClosestPole();
		if (degrees == null) {
			return "?";
		}
		return String.format(Locale.ENGLISH, "%.0f°", degrees);
	}

}
