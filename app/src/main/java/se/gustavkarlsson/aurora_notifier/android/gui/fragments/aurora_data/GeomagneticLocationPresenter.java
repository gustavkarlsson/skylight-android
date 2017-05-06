package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;

class GeomagneticLocationPresenter extends AbstractAuroraDataPresenter {
	GeomagneticLocationPresenter(AuroraDataView dataView) {
		super(dataView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.geomagnetic_location_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.geomagnetic_location_desc;
	}

	void onUpdate(GeomagneticLocation geomagneticLocation) {
		float degreesFromClosestPole = geomagneticLocation.getDegreesFromClosestPole();
		setDataValue(String.format(Locale.ENGLISH, "%.2f°", degreesFromClosestPole));
	}
}
