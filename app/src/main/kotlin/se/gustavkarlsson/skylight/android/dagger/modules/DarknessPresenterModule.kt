package se.gustavkarlsson.skylight.android.dagger.modules

import android.view.View
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services_impl.presenters.factors.DarknessFactorViewPresenter
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import javax.inject.Named

@Module
class DarknessPresenterModule {

    @Provides
    @FragmentScope
    fun provideDarknessPresenter(
		@Named(FRAGMENT_ROOT_NAME) rootView: View,
		chanceEvaluator: ChanceEvaluator<Darkness>,
		chanceToColorConverter: ChanceToColorConverter
	): Presenter<Darkness> {
        val darknessView = rootView.findViewById<AuroraFactorView>(R.id.darkness)
        return DarknessFactorViewPresenter(darknessView, chanceEvaluator, chanceToColorConverter)
    }
}
