package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.View
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import se.gustavkarlsson.skylight.android.services_impl.presenters.factors.GeomagLocationFactorViewPresenter
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import javax.inject.Named

@Module
class GeomagLocationPresenterModule {

    // Published
    @Provides
    @FragmentScope
    fun provideGeomagLocationPresenter(
		@Named(FRAGMENT_ROOT_NAME) rootView: View,
		chanceEvaluator: ChanceEvaluator<GeomagLocation>,
		chanceToColorConverter: ChanceToColorConverter
	): Presenter<GeomagLocation> {
        val geomagLocationView = rootView.findViewById(R.id.gGeomagLocation) as AuroraFactorView
        return GeomagLocationFactorViewPresenter(geomagLocationView, chanceEvaluator, chanceToColorConverter)
    }
}
