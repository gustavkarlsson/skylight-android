package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.view.View;
import android.widget.TextView;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.ChancePresenter;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule.FRAGMENT_ROOT_NAME;

@Module
public class ChancePresenterModule {

	// Published
	@Provides
	@FragmentScope
	ChancePresenter provideChancePresenter(@Named(FRAGMENT_ROOT_NAME) View rootView) {
		TextView chanceView = (TextView) rootView.findViewById(R.id.chance);
		return new ChancePresenter(chanceView);
	}
}
