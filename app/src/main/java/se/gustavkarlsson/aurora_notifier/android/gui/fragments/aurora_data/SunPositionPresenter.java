package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;

class SunPositionPresenter extends AbstractAuroraDataPresenter {
	SunPositionPresenter(AuroraDataView dataView) {
		super(dataView);
	}

	void update(SunPosition sunPosition) {
		float zenithAngle = sunPosition.getZenithAngle();
		setDataValue(String.format(Locale.getDefault(), "%.2f°", zenithAngle));
	}
}
