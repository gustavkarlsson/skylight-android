package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.view.View;
import android.widget.TextView;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.TimeSinceUpdatePresenter;

import static se.gustavkarlsson.skylight.android.dagger.Names.FRAGMENT_ROOT_NAME;

@Module
public class TimeSinceUpdatePresenterModule {

	// Published
	@Provides
	@FragmentScope
	TimeSinceUpdatePresenter provideTimeSinceUpdatePresenter(@Named(FRAGMENT_ROOT_NAME) View rootView, Clock clock) {
		TextView timeSinceUpdateView = (TextView) rootView.findViewById(R.id.time_since_update);
		return new TimeSinceUpdatePresenter(timeSinceUpdateView, Duration.ofMinutes(1), clock);
	}
}
