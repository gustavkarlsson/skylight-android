package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.Updater;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;
import se.gustavkarlsson.skylight.android.gui.activities.main.SwipeToRefreshPresenter;

import static se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME;

@Module(includes = ActivityModule.class)
public abstract class SwipeToRefreshModule {

	// Published
	@Provides
	@ActivityScope
	static SwipeToRefreshPresenter provideSwipeToRefreshPresenter(Activity activity, Updater updater, @Named(CACHED_THREAD_POOL_NAME) ExecutorService cachedThreadPool) {
		SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
		return new SwipeToRefreshPresenter(swipeRefreshLayout, activity, updater, cachedThreadPool);
	}
}
