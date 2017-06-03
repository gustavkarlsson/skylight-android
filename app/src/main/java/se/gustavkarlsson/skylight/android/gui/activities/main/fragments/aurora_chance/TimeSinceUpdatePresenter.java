package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.text.format.DateUtils;
import android.widget.TextView;

import org.threeten.bp.Clock;

import java.util.Timer;
import java.util.TimerTask;

import se.gustavkarlsson.skylight.android.R;

public class TimeSinceUpdatePresenter {
	private final TextView timeSinceUpdateTextView;
	private final long updateTimeResolutionMillis;
	private final Clock clock;
	private Timer timeUpdateTimer;
	private long lastUpdateMillis;

	public TimeSinceUpdatePresenter(TextView timeSinceUpdateTextView, long updateTimeResolutionMillis, Clock clock) {
		this.timeSinceUpdateTextView = timeSinceUpdateTextView;
		this.updateTimeResolutionMillis = updateTimeResolutionMillis;
		this.clock = clock;
	}

	synchronized void start() {
		rescheduleRefresh();
		updateTimeSinceUpdate();
	}

	synchronized void update(long lastUpdateMillis) {
		this.lastUpdateMillis = lastUpdateMillis;
		rescheduleRefresh();
		updateTimeSinceUpdate();
	}

	private void rescheduleRefresh() {
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
		// The 1000 ms makes sure that we're past the minute line
	}

	private void updateTimeSinceUpdate() {
		if (isRightNow(lastUpdateMillis)) {
			timeSinceUpdateTextView.setText(R.string.right_now);
			return;
		}
		CharSequence text = formatRelativeTime(lastUpdateMillis);
		timeSinceUpdateTextView.setText(text);
	}

	private boolean isRightNow(long timeMillis) {
		long ageMillis = clock.millis() - timeMillis;
		return ageMillis <= updateTimeResolutionMillis;
	}

	private CharSequence formatRelativeTime(long startTimeMillis) {
		return DateUtils.getRelativeTimeSpanString(startTimeMillis, clock.millis(), updateTimeResolutionMillis);
	}

	synchronized void stop() {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
	}
}
