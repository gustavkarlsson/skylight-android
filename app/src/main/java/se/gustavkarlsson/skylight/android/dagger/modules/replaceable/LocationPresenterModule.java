package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.view.View;
import android.widget.TextView;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.LocationPresenter;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

@Module
public class LocationPresenterModule {

	// Published
	@Provides
	@FragmentScope
	LocationPresenter provideLocationPresenter(@Named(FRAGMENT_ROOT_NAME) View rootView) {
		TextView locationView = (TextView) rootView.findViewById(R.id.location);
		return new LocationPresenter(locationView);
	}
}
