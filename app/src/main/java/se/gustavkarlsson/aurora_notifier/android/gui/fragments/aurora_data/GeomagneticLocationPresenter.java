package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;

class GeomagneticLocationPresenter extends AbstractAuroraDataPresenter {
	GeomagneticLocationPresenter(AuroraDataView dataView) {
		super(dataView);
	}

	void update(GeomagneticLocation geomagneticLocation) {
		float degreesFromClosestPole = geomagneticLocation.getDegreesFromClosestPole();
		setDataValue(String.format(Locale.getDefault(), "%.2f°", degreesFromClosestPole));
	}
}
