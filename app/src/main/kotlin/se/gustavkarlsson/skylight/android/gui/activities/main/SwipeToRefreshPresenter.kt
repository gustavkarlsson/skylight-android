package se.gustavkarlsson.skylight.android.gui.activities.main

import android.support.v4.widget.SwipeRefreshLayout
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport

class SwipeToRefreshPresenter(
	private val swipeRefreshLayout: SwipeRefreshLayout,
	private val showNewAuroraReport: ShowNewAuroraReport
) {

    private fun triggerUpdate() {
		async(UI) {
			swipeRefreshLayout.isRefreshing = true
			val update = bg {
				showNewAuroraReport()
			}
			update.await()
			swipeRefreshLayout.isRefreshing = false
		}
    }

    fun enable() {
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener(this::triggerUpdate)
    }

    fun disable() {
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener(null)
        swipeRefreshLayout.isEnabled = false
    }
}
