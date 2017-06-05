package se.gustavkarlsson.skylight.android.gui.activities.main;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;

import org.threeten.bp.Duration;

import java.util.concurrent.ExecutorService;

import se.gustavkarlsson.skylight.android.background.Updater;

public class SwipeToRefreshPresenter {
	private final SwipeRefreshLayout swipeRefreshLayout;
	private final Activity activity;
	private final Updater updater;
	private final ExecutorService cachedThreadPool;
	private final Duration timeout;

	public SwipeToRefreshPresenter(SwipeRefreshLayout swipeRefreshLayout, Activity activity, Updater updater, ExecutorService cachedThreadPool, Duration timeout) {
		this.swipeRefreshLayout = swipeRefreshLayout;
		this.activity = activity;
		this.updater = updater;
		this.cachedThreadPool = cachedThreadPool;
		this.timeout = timeout;
	}

	private void update() {
		swipeRefreshLayout.setRefreshing(true);
		cachedThreadPool.execute(() -> {
			updater.update(timeout.toMillis());
			activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
        });
	}

	void enable() {
		swipeRefreshLayout.setEnabled(true);
		swipeRefreshLayout.setRefreshing(false);
		swipeRefreshLayout.setOnRefreshListener(this::update);
	}

	void disable() {
		swipeRefreshLayout.setRefreshing(false);
		swipeRefreshLayout.setOnRefreshListener(null);
		swipeRefreshLayout.setEnabled(false);
	}
}
