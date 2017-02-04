package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;

class SwipeToRefreshPresenter {
	private static final String TAG = SwipeToRefreshPresenter.class.getSimpleName();

	private final ServiceConnection updaterConnection = new UpdaterConnection();
	private final Context context;
	private final SwipeRefreshLayout swipeRefreshLayout;

	private Updater updater;
	private boolean updaterBound;

	SwipeToRefreshPresenter(SwipeRefreshLayout swipeRefreshLayout) {
		this.context = swipeRefreshLayout.getContext();
		this.swipeRefreshLayout = swipeRefreshLayout;

		// TODO extract inner class
		swipeRefreshLayout.setOnRefreshListener(() -> {
			if (updaterBound) {
				swipeRefreshLayout.setRefreshing(true);
				AsyncTask.execute(updater::update);
			}
		});
	}

	void setRefreshing(boolean refreshing) {
		swipeRefreshLayout.setRefreshing(refreshing);
	}

	void start() {
		setRefreshing(false);
		Intent intent = new Intent(context, UpdateService.class);
		context.bindService(intent, updaterConnection, Context.BIND_AUTO_CREATE);
	}

	void stop() {
		setRefreshing(false);
		if (updaterBound) {
			context.unbindService(updaterConnection);
			updaterBound = false;
		}
	}

	private class UpdaterConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(TAG, "onServiceConnected");
			UpdateService.UpdaterBinder updaterBinder = (UpdateService.UpdaterBinder) service;
			updater = updaterBinder.getUpdater();
			updaterBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v(TAG, "onServiceDisconnected");
			updaterBound = false;
		}

	}
}
