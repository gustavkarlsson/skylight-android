package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;

class SwipeToRefreshPresenter {
	private final Context context;
	private final SwipeRefreshLayout swipeRefreshLayout;

	SwipeToRefreshPresenter(SwipeRefreshLayout swipeRefreshLayout) {
		this.context = swipeRefreshLayout.getContext();
		this.swipeRefreshLayout = swipeRefreshLayout;

		// TODO extract inner class
		swipeRefreshLayout.setOnRefreshListener(() -> {
			swipeRefreshLayout.setRefreshing(true);
			UpdateScheduler.setupUpdateScheduling(context, false);
		});
	}

	void setRefreshing(boolean refreshing) {
		swipeRefreshLayout.setRefreshing(refreshing);
	}

	void onStart() {
		setRefreshing(false);
	}

	void onStop() {
		setRefreshing(false);
	}
}
