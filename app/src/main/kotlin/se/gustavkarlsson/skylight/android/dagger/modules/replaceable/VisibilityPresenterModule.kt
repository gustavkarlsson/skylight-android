package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.View
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.Names.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.VisibilityPresenter
import se.gustavkarlsson.skylight.android.models.Visibility
import javax.inject.Named

@Module
class VisibilityPresenterModule {

    // Published
    @Provides
    @FragmentScope
    fun provideVisibilityPresenter(
			@Named(FRAGMENT_ROOT_NAME) rootView: View,
			chanceEvaluator: ChanceEvaluator<Visibility>,
			chanceToColorConverter: ChanceToColorConverter
	): VisibilityPresenter {
        val geomagActivityView = rootView.findViewById(R.id.aurora_factor_visibility) as AuroraFactorView
        return VisibilityPresenter(geomagActivityView, chanceEvaluator, chanceToColorConverter)
    }
}
