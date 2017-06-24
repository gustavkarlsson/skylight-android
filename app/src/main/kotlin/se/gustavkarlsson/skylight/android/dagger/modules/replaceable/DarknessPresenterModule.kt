package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.View
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.DarknessPresenter
import se.gustavkarlsson.skylight.android.models.Darkness
import javax.inject.Named

@Module
class DarknessPresenterModule {

    // Published
    @Provides
    @FragmentScope
    fun provideDarknessPresenter(
			@Named(FRAGMENT_ROOT_NAME) rootView: View,
			chanceEvaluator: ChanceEvaluator<Darkness>,
			chanceToColorConverter: ChanceToColorConverter
	): DarknessPresenter {
        val darknessView = rootView.findViewById(R.id.darkness) as AuroraFactorView
        return DarknessPresenter(darknessView, chanceEvaluator, chanceToColorConverter)
    }
}
