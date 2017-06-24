package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.app.Activity
import android.support.v4.widget.SwipeRefreshLayout
import dagger.Module
import dagger.Provides
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.Updater
import se.gustavkarlsson.skylight.android.dagger.FOREGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.activities.main.SwipeToRefreshPresenter
import javax.inject.Named

@Module(includes = arrayOf(ActivityModule::class))
class SwipeToRefreshModule {

    // Published
    @Provides
    @ActivityScope
    fun provideSwipeToRefreshPresenter(
			activity: Activity,
			updater: Updater,
			@Named(FOREGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration
	): SwipeToRefreshPresenter {
        val swipeRefreshLayout = activity.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        return SwipeToRefreshPresenter(swipeRefreshLayout, updater, timeout)
    }
}
