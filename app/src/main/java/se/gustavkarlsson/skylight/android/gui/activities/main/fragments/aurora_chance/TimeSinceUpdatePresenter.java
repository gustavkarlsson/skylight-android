package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance;

import android.text.format.DateUtils;
import android.widget.TextView;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.util.Timer;
import java.util.TimerTask;

import se.gustavkarlsson.skylight.android.R;

public class TimeSinceUpdatePresenter {
	private final TextView timeSinceUpdateTextView;
	private final Duration updateTimeResolution;
	private final Clock clock;
	private Timer timeUpdateTimer;
	private Instant lastUpdate;

	public TimeSinceUpdatePresenter(TextView timeSinceUpdateTextView, Duration updateTimeResolution, Clock clock) {
		this.timeSinceUpdateTextView = timeSinceUpdateTextView;
		this.updateTimeResolution = updateTimeResolution;
		this.clock = clock;
	}

	synchronized void start() {
		rescheduleRefresh();
		updateTimeSinceUpdate();
	}

	synchronized void update(Instant lastUpdate) {
		this.lastUpdate = lastUpdate;
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
		}, Duration.ofSeconds(1).toMillis(), updateTimeResolution.toMillis());
		// The 1 second delay makes sure that we're past the minute line
	}

	private void updateTimeSinceUpdate() {
		if (isRightNow(lastUpdate)) {
			timeSinceUpdateTextView.setText(R.string.right_now);
			return;
		}
		CharSequence text = formatRelativeTime(lastUpdate);
		timeSinceUpdateTextView.setText(text);
	}

	private boolean isRightNow(Instant time) {
		Instant now = clock.instant();
		// TODO change to until
		Duration age = Duration.between(time, now);
		return !updateTimeResolution.minus(age).isNegative();
	}

	private CharSequence formatRelativeTime(Instant time) {
		return DateUtils.getRelativeTimeSpanString(time.toEpochMilli(), clock.millis(), updateTimeResolution.toMillis());
	}

	synchronized void stop() {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
	}
}
