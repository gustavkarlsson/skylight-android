package se.gustavkarlsson.skylight.android.gui.activities.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import org.threeten.bp.Duration;

import se.gustavkarlsson.skylight.android.background.Updater;

public class SwipeToRefreshPresenter {
	private static final Duration TIMEOUT = Duration.ofSeconds(10);

	private final SwipeRefreshLayout swipeRefreshLayout;
	private final Activity activity;
	private final Updater updater;

	public SwipeToRefreshPresenter(SwipeRefreshLayout swipeRefreshLayout, Activity activity, Updater updater) {
		this.swipeRefreshLayout = swipeRefreshLayout;
		this.activity = activity;
		this.updater = updater;
	}

	private void update(SwipeRefreshLayout swipeRefreshLayout, Activity activity) {
		swipeRefreshLayout.setRefreshing(true);
		AsyncTask.execute(() -> {
			updater.update(TIMEOUT.toMillis());
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
