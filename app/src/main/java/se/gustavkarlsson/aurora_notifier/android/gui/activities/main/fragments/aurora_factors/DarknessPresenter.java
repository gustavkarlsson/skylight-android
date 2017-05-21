package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerEvaluationComponent;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

class DarknessPresenter extends AbstractAuroraFactorPresenter<Darkness> {

	DarknessPresenter(AuroraFactorView factorView) {
		super(factorView, DaggerEvaluationComponent.create().getDarknessEvaluator());
	}

	@Override
	int getFullTitleResourceId() {
		return R.string.factor_darkness_title_full;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_darkness_desc;
	}

	/*
	 * 0% at 90°. 100% at 108°
	 */
	@Override
	String evaluateText(Darkness darkness) {
		Float zenithAngle = darkness.getSunZenithAngle();
		if (zenithAngle == null) {
			return "?";
		}
		double darknessFactor = (1.0 / 18.0) * zenithAngle - 5.0;
		long darknessPercentage = round(darknessFactor * 100.0);
		darknessPercentage = max(0, darknessPercentage);
		darknessPercentage = min(100, darknessPercentage);
		return "" + darknessPercentage + "%";
	}
}
