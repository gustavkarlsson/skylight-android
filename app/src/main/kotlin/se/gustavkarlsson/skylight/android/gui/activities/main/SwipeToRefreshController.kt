package se.gustavkarlsson.skylight.android.gui.activities.main

import android.support.v4.widget.SwipeRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.actions.GetNewAuroraReport

class SwipeToRefreshController(
	private val swipeRefreshLayout: SwipeRefreshLayout,
	private val getNewAuroraReport: GetNewAuroraReport
) {

	private fun refresh() {
		swipeRefreshLayout.isRefreshing = true
		getNewAuroraReport()
			.subscribeOn(Schedulers.io())
			.onErrorComplete()
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe {
				swipeRefreshLayout.isRefreshing = false
			}
	}

	fun enable() {
		swipeRefreshLayout.isEnabled = true
		swipeRefreshLayout.isRefreshing = false
		swipeRefreshLayout.setOnRefreshListener(this::refresh)
	}

	fun disable() {
		swipeRefreshLayout.isRefreshing = false
		swipeRefreshLayout.setOnRefreshListener(null)
		swipeRefreshLayout.isEnabled = false
	}
}
