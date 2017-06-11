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
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.GeomagLocationPresenter
import se.gustavkarlsson.skylight.android.models.GeomagLocation
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
	): GeomagLocationPresenter {
        val geomagLocationView = rootView.findViewById(R.id.aurora_factor_geomag_location) as AuroraFactorView
        return GeomagLocationPresenter(geomagLocationView, chanceEvaluator, chanceToColorConverter)
    }
}
