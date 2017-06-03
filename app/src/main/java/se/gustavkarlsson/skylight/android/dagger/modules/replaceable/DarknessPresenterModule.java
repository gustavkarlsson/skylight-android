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
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.DarknessPresenter;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

@Module
public class DarknessPresenterModule {

	// Published
	@Provides
	@FragmentScope
	DarknessPresenter provideDarknessPresenter(@Named(FRAGMENT_ROOT_NAME) View rootView, ChanceEvaluator<Darkness> chanceEvaluator, ChanceToColorConverter chanceToColorConverter) {
		AuroraFactorView darknessView = (AuroraFactorView) rootView.findViewById(R.id.aurora_factor_darkness);
		return new DarknessPresenter(darknessView, chanceEvaluator, chanceToColorConverter);
	}
}
