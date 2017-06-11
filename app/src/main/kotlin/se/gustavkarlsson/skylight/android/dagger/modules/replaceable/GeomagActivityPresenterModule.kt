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
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.GeomagActivityPresenter
import se.gustavkarlsson.skylight.android.models.GeomagActivity
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
	): GeomagActivityPresenter {
        val geomagActivityView = rootView.findViewById(R.id.aurora_factor_geomag_activity) as AuroraFactorView
        return GeomagActivityPresenter(geomagActivityView, chanceEvaluator, chanceToColorConverter)
    }
}
