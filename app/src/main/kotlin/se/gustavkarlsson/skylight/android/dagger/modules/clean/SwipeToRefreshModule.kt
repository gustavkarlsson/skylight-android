package se.gustavkarlsson.skylight.android.dagger.modules.clean

import android.app.Activity
import android.support.v4.widget.SwipeRefreshLayout
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.PresentNewAuroraReport
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.PresentNewAuroraReportModule
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.activities.main.SwipeToRefreshController

@Module(includes = arrayOf(
	ActivityModule::class,
	PresentNewAuroraReportModule::class
))
class SwipeToRefreshModule {

    // Published
    @Provides
    @ActivityScope
    fun provideSwipeToRefreshPresenter(
		activity: Activity,
		presentNewAuroraReport: PresentNewAuroraReport
	): SwipeToRefreshController {
        val swipeRefreshLayout = activity.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        return SwipeToRefreshController(swipeRefreshLayout, presentNewAuroraReport)
    }
}
