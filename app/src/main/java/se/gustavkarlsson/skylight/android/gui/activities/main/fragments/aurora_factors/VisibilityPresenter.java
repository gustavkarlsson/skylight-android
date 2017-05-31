package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.dagger.components.DaggerEvaluationComponent;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

class VisibilityPresenter extends AbstractAuroraFactorPresenter<Visibility> {

	VisibilityPresenter(AuroraFactorView factorView) {
		super(factorView, DaggerEvaluationComponent.create().getVisibilityEvaluator());
	}

	@Override
	int getFullTitleResourceId() {
		return R.string.factor_visibility_title_full;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_visibility_desc;
	}

	@Override
	String evaluateText(Visibility visibility) {
		Integer clouds = visibility.getCloudPercentage();
		if (clouds == null) {
			return "?";
		}
		int visibilityPercentage = 100 - clouds;
		return Integer.toString(visibilityPercentage) + '%';
	}
}
