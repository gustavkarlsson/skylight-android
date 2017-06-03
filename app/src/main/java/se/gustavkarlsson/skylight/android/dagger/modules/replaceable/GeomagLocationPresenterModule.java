package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.view.View;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.GeomagLocationPresenter;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

@Module
public class GeomagLocationPresenterModule {

	// Published
	@Provides
	@FragmentScope
	GeomagLocationPresenter provideGeomagLocationPresenter(@Named(FRAGMENT_ROOT_NAME) View rootView) {
		AuroraFactorView geomagLocationView = (AuroraFactorView) rootView.findViewById(R.id.aurora_factor_geomag_location);
		return new GeomagLocationPresenter(geomagLocationView);
	}
}
