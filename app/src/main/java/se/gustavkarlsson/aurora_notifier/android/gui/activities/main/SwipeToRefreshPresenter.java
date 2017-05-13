package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;

class SwipeToRefreshPresenter {
	private final SwipeRefreshLayout swipeRefreshLayout;
	private final Activity activity;
	private final Updater updater;
	private final int timeoutMillis;

	SwipeToRefreshPresenter(SwipeRefreshLayout swipeRefreshLayout, Activity activity, Updater updater) {
		this.swipeRefreshLayout = swipeRefreshLayout;
		this.activity = activity;
		this.updater = updater;
		this.timeoutMillis = activity.getResources().getInteger(R.integer.setting_foreground_update_timeout_millis);
	}

	private void update(SwipeRefreshLayout swipeRefreshLayout, Activity activity) {
		swipeRefreshLayout.setRefreshing(true);
		AsyncTask.execute(() -> {
			updater.update(timeoutMillis);
			activity.runOnUiThread(() -> setRefreshing(false));
        });
	}

	void enable() {
		swipeRefreshLayout.setEnabled(true);
		setRefreshing(false);
		swipeRefreshLayout.setOnRefreshListener(() -> update(swipeRefreshLayout, activity));
	}

	void disable() {
		setRefreshing(false);
		swipeRefreshLayout.setOnRefreshListener(null);
		swipeRefreshLayout.setEnabled(false);
	}

	private void setRefreshing(boolean refreshing) {
		swipeRefreshLayout.setRefreshing(refreshing);
	}
}
