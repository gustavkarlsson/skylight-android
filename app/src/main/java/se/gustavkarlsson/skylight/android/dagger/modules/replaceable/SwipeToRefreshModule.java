package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.Updater;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.SwipeToRefreshPresenter;

@Module(includes = ActivityModule.class)
public class SwipeToRefreshModule {

	@Provides
	@ActivityScope
	SwipeToRefreshPresenter provideSwipeToRefreshPresenter(Activity activity, Updater updater) {
		SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
		return new SwipeToRefreshPresenter(swipeRefreshLayout, activity, updater);
	}
}
