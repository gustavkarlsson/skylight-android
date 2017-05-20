package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.SunPositionEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;

class SunPositionPresenter extends AbstractAuroraFactorPresenter<SunPosition> {

	SunPositionPresenter(AuroraFactorView factorView) {
		super(factorView, new SunPositionEvaluator());
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_sun_position_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_sun_position_desc;
	}

	@Override
	String evaluateText(SunPosition sunPosition) {
		Float zenithAngle = sunPosition.getZenithAngle();
		if (zenithAngle == null) {
			return "?";
		}
		return String.format(Locale.ENGLISH, "%.0f°", zenithAngle);
	}
}
