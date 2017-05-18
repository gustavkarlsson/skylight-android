package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.SunPositionEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;

class SunPositionPresenter extends AbstractAuroraFactorPresenter {
	private final SunPositionEvaluator evaluator = new SunPositionEvaluator();

	SunPositionPresenter(AuroraFactorView factorView) {
		super(factorView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_sun_position_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_sun_position_desc;
	}

	void onUpdate(SunPosition sunPosition) {
		setBackgroundColor(evaluator.evaluate(sunPosition));
		setFactorValue(evaluateText(sunPosition));
	}

	private static String evaluateText(SunPosition sunPosition) {
		float zenithAngle = sunPosition.getZenithAngle();
		return String.format(Locale.ENGLISH, "%.0f°", zenithAngle);
	}
}
