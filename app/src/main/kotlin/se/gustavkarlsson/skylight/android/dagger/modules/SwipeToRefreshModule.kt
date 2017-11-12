package se.gustavkarlsson.skylight.android.dagger.modules

import android.app.Activity
import android.support.v4.widget.SwipeRefreshLayout
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.GetNewAuroraReport
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.activities.main.SwipeToRefreshController

@Module
class SwipeToRefreshModule {

	@Provides
	@ActivityScope
	fun provideSwipeToRefreshPresenter(
		activity: Activity,
		getNewAuroraReport: GetNewAuroraReport
	): SwipeToRefreshController {
		val swipeRefreshLayout = activity.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
		return SwipeToRefreshController(swipeRefreshLayout, getNewAuroraReport)
	}
}
