package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.View
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.entities.GeomagActivity
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.services_impl.presenters.factors.GeomagActivityFactorViewPresenter
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import javax.inject.Named

@Module
class GeomagActivityPresenterModule {

    // Published
    @Provides
    @FragmentScope
    fun provideGeomagActivityPresenter(
		@Named(FRAGMENT_ROOT_NAME) rootView: View,
		chanceEvaluator: ChanceEvaluator<GeomagActivity>,
		chanceToColorConverter: ChanceToColorConverter
	): Presenter<GeomagActivity> {
        val geomagActivityView = rootView.findViewById(R.id.geomagActivity) as AuroraFactorView
        return GeomagActivityFactorViewPresenter(geomagActivityView, chanceEvaluator, chanceToColorConverter)
    }
}
