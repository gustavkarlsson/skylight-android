package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.text.format.DateUtils;
import android.widget.TextView;

import org.threeten.bp.Clock;

import java.util.Timer;
import java.util.TimerTask;

import se.gustavkarlsson.skylight.android.R;

class TimeSinceUpdatePresenter {
	private final TextView timeSinceUpdateTextView;
	private final long updateTimeResolutionMillis;
	private final Clock clock;
	private Timer timeUpdateTimer;
	private long lastUpdateMillis;

	TimeSinceUpdatePresenter(TextView timeSinceUpdateTextView, long updateTimeResolutionMillis, Clock clock) {
		this.timeSinceUpdateTextView = timeSinceUpdateTextView;
		this.updateTimeResolutionMillis = updateTimeResolutionMillis;
		this.clock = clock;
	}

	synchronized void onStart() {
		scheduleTimeSinceUpdateRefresh();
		updateTimeSinceUpdate();
	}

	synchronized void onUpdate(long lastUpdateMillis) {
		this.lastUpdateMillis = lastUpdateMillis;
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
			timeSinceUpdateTextView.setText(R.string.right_now);
			return;
		}
		CharSequence text = formatRelativeTime(lastUpdateMillis);
		timeSinceUpdateTextView.setText(text);
		timeSinceUpdateTextView.invalidate();
	}

	private boolean isJustNow(long timeMillis) {
		long ageMillis = clock.millis() - timeMillis;
		return ageMillis <= updateTimeResolutionMillis;
	}

	private CharSequence formatRelativeTime(long startTimeMillis) {
		return DateUtils.getRelativeTimeSpanString(startTimeMillis, clock.millis(), updateTimeResolutionMillis);
	}

	synchronized void destroy() {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
	}
}
