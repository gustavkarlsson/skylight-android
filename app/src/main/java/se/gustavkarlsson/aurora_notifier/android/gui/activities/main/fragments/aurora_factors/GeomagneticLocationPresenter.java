package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;

class GeomagneticLocationPresenter extends AbstractAuroraFactorPresenter {
	GeomagneticLocationPresenter(AuroraFactorView factorView) {
		super(factorView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_geomagnetic_location_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_geomagnetic_location_desc;
	}

	void onUpdate(GeomagneticLocation geomagneticLocation) {
		float degreesFromClosestPole = geomagneticLocation.getDegreesFromClosestPole();
		setFactorValue(String.format(Locale.ENGLISH, "%.2f°", degreesFromClosestPole));
	}
}
