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

	TimeSinceUpdatePresenter(TextView timeSinceUpdateTextView, long updateTimeResolutionMillis) {
		this.timeSinceUpdateTextView = timeSinceUpdateTextView;
		this.updateTimeResolutionMillis = updateTimeResolutionMillis;
	}

	void update(long lastUpdateMillis) {
		scheduleTimeSinceUpdateRefresh(lastUpdateMillis);
		updateTimeSinceUpdate(lastUpdateMillis);
	}

	private void scheduleTimeSinceUpdateRefresh(long updateTimestampMillis) {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
		timeUpdateTimer = new Timer();
		timeUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeSinceUpdateTextView.post(() -> updateTimeSinceUpdate(updateTimestampMillis));
			}
		}, 1000L, updateTimeResolutionMillis);
	}

	private void updateTimeSinceUpdate(long updateTimeMillis) {
		if (isJustNow(updateTimeMillis)) {
			timeSinceUpdateTextView.setText(R.string.just_now);
			return;
		}
		CharSequence text = formatRelativeTime(updateTimeMillis);
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
