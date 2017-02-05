package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_chance;

import android.text.format.DateUtils;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import se.gustavkarlsson.aurora_notifier.android.R;

class TimeSinceUpdatePresenter {
	private final TextView timeSinceUpdateTextView;
	private final long updateTimeResolutionMillis;
	private Timer timeUpdateTimer;
	private long lastUpdateMillis;

	TimeSinceUpdatePresenter(TextView timeSinceUpdateTextView, long updateTimeResolutionMillis) {
		this.timeSinceUpdateTextView = timeSinceUpdateTextView;
		this.updateTimeResolutionMillis = updateTimeResolutionMillis;
	}

	void onUpdate(long lastUpdateMillis) {
		this.lastUpdateMillis = lastUpdateMillis;
		scheduleTimeSinceUpdateRefresh();
		updateTimeSinceUpdate();
	}

	void onStart() {
		scheduleTimeSinceUpdateRefresh();
		updateTimeSinceUpdate();
	}

	private void scheduleTimeSinceUpdateRefresh() {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
		timeUpdateTimer = new Timer();
		timeUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeSinceUpdateTextView.post(() -> updateTimeSinceUpdate());
			}
		}, 1000L, updateTimeResolutionMillis);
	}

	private void updateTimeSinceUpdate() {
		if (lastUpdateMillis == 0) {
			return;
		}
		if (isJustNow(lastUpdateMillis)) {
			timeSinceUpdateTextView.setText(R.string.just_now);
			return;
		}
		CharSequence text = formatRelativeTime(lastUpdateMillis);
		timeSinceUpdateTextView.setText(text);
		timeSinceUpdateTextView.invalidate();
	}

	private boolean isJustNow(long timeMillis) {
		long ageMillis = System.currentTimeMillis() - timeMillis;
		return ageMillis <= updateTimeResolutionMillis;
	}

	private CharSequence formatRelativeTime(long startTimeMillis) {
		return DateUtils.getRelativeTimeSpanString(startTimeMillis, System.currentTimeMillis(), updateTimeResolutionMillis);
	}

	void destroy() {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
	}
}
