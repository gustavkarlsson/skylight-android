package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.text.format.DateUtils
import android.widget.TextView
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.until
import java.util.*

class TimeSinceUpdatePresenter(
		private val timeSinceUpdateTextView: TextView,
		private val updateTimeResolution: Duration,
		private val clock: Clock
) {
    private var timeUpdateTimer: Timer? = null
    private var lastUpdate: Instant? = null

    @Synchronized
	fun start() {
        rescheduleRefresh()
		updateTimeSinceUpdate(lastUpdate)
    }

    @Synchronized
	fun update(lastUpdate: Instant) {
        this.lastUpdate = lastUpdate
        rescheduleRefresh()
        updateTimeSinceUpdate(lastUpdate)
    }

	@Synchronized
	fun stop() {
		timeUpdateTimer?.cancel()
	}

    private fun rescheduleRefresh() {
		timeUpdateTimer?.cancel()
        timeUpdateTimer = Timer()
        timeUpdateTimer?.schedule(object : TimerTask() {
            override fun run() {
                timeSinceUpdateTextView.post {
					updateTimeSinceUpdate(lastUpdate)
				}
            }
        }, Duration.ofSeconds(1).toMillis(), updateTimeResolution.toMillis())
        // The 1 second delay makes sure that we're past the minute line
    }

    private fun updateTimeSinceUpdate(update: Instant?) {
		update?: return
		if (isRightNow(update)) {
            timeSinceUpdateTextView.setText(R.string.right_now)
        } else {
			timeSinceUpdateTextView.text = formatRelativeTime(update)
		}
    }

    private fun isRightNow(time: Instant): Boolean {
        val age = time until clock.now
		return age < updateTimeResolution
    }

    private fun formatRelativeTime(time: Instant): CharSequence {
        return DateUtils.getRelativeTimeSpanString(time.toEpochMilli(), clock.millis(), updateTimeResolution.toMillis())
    }
}
