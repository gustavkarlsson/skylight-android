package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;

class SunPositionPresenter extends AbstractAuroraDataPresenter {
	SunPositionPresenter(AuroraDataView dataView) {
		super(dataView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.sun_position_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.sun_position_desc;
	}

	void onUpdate(SunPosition sunPosition) {
		float zenithAngle = sunPosition.getZenithAngle();
		setDataValue(String.format(Locale.getDefault(), "%.2f°", zenithAngle));
	}
}
