package se.gustavkarlsson.skylight.android.gui.activities.main

import android.app.Activity
import android.support.v4.widget.SwipeRefreshLayout
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.Updater
import java.util.concurrent.ExecutorService

class SwipeToRefreshPresenter(
		private val swipeRefreshLayout: SwipeRefreshLayout,
		private val activity: Activity,
		private val updater: Updater,
		private val cachedThreadPool: ExecutorService,
		private val timeout: Duration
) {

    private fun update() {
        swipeRefreshLayout.isRefreshing = true
        cachedThreadPool.execute {
            updater.update(timeout)
            activity.runOnUiThread { swipeRefreshLayout.isRefreshing = false }
        }
    }

    fun enable() {
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener(this::update)
    }

    fun disable() {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener(null)
        swipeRefreshLayout.isEnabled = false
    }
}
