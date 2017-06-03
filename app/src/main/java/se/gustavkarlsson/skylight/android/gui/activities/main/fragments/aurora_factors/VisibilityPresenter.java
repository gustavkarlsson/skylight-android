package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

public class VisibilityPresenter extends AbstractAuroraFactorPresenter<Visibility> {

	public VisibilityPresenter(AuroraFactorView factorView, ChanceEvaluator<Visibility> chanceEvaluator, ChanceToColorConverter colorConverter) {
		super(factorView, chanceEvaluator, colorConverter);
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
