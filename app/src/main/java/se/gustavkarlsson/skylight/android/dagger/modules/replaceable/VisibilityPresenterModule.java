package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.view.View;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.ChanceToColorConverter;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.VisibilityPresenter;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

import static se.gustavkarlsson.skylight.android.dagger.Names.FRAGMENT_ROOT_NAME;

@Module
public class VisibilityPresenterModule {

	// Published
	@Provides
	@FragmentScope
	VisibilityPresenter provideVisibilityPresenter(@Named(FRAGMENT_ROOT_NAME) View rootView, ChanceEvaluator<Visibility> chanceEvaluator, ChanceToColorConverter chanceToColorConverter) {
		AuroraFactorView geomagActivityView = (AuroraFactorView) rootView.findViewById(R.id.aurora_factor_visibility);
		return new VisibilityPresenter(geomagActivityView, chanceEvaluator, chanceToColorConverter);
	}
}
