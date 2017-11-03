package se.gustavkarlsson.skylight.android.dagger.modules

import android.view.View
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services_impl.presenters.factors.KpIndexFactorViewPresenter
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import javax.inject.Named

@Module
class KpIndexPresenterModule {

    @Provides
    @FragmentScope
    fun provideKpIndexPresenter(
		@Named(FRAGMENT_ROOT_NAME) rootView: View,
		chanceEvaluator: ChanceEvaluator<KpIndex>,
		chanceToColorConverter: ChanceToColorConverter
	): Presenter<KpIndex> {
        val kpIndexView = rootView.findViewById<AuroraFactorView>(R.id.kpIndex)
        return KpIndexFactorViewPresenter(kpIndexView, chanceEvaluator, chanceToColorConverter)
    }
}
