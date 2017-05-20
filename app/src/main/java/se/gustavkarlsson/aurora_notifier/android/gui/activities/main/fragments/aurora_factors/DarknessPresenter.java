package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.DarknessEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

class DarknessPresenter extends AbstractAuroraFactorPresenter<Darkness> {

	DarknessPresenter(AuroraFactorView factorView) {
		super(factorView, new DarknessEvaluator());
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_darkness_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_darkness_desc;
	}

	@Override
	String evaluateText(Darkness darkness) {
		Float zenithAngle = darkness.getSunZenithAngle();
		if (zenithAngle == null) {
			return "?";
		}
		double darknessFactor = (1.0 / 12.0) * zenithAngle - 8.0;
		long darknessPercentage = round(darknessFactor) * 100;
		darknessPercentage = max(0, darknessPercentage);
		darknessPercentage = min(100, darknessPercentage);
		return "" + darknessPercentage + "%";
	}
}
