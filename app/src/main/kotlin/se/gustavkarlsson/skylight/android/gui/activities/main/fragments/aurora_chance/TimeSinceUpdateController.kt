package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.text.format.DateUtils
import android.widget.TextView
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.threeten.bp.*
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.until

class TimeSinceUpdateController(
		private val updateTimeResolution: Duration,
		timeSinceUpdateTextView: TextView,
		clock: Clock
) {
	private val presenter = TimeSinceUpdatePresenter(timeSinceUpdateTextView, updateTimeResolution, clock)
    private var refresher: Job? = null
    private var lastUpdate: Instant? = null

    @Synchronized
	fun start() {
		rescheduleRefresh()
    }

    @Synchronized
	fun update(lastUpdate: Instant) {
        this.lastUpdate = lastUpdate
        rescheduleRefresh()
    }

	@Synchronized
	fun stop() {
		refresher?.cancel()
	}

	private fun rescheduleRefresh() {
		refresher?.cancel()
		refresher = launch(CommonPool) {
			while (true) {
				presenter.present(lastUpdate)
				delay(updateTimeResolution.toMillis())
			}
		}
    }
}

private class TimeSinceUpdatePresenter(
		private val timeSinceUpdateTextView: TextView,
		private val updateTimeResolution: Duration,
		private val clock: Clock
) {

	fun present(time: Instant?) {
		async(UI) {
			when {
				time == null     -> timeSinceUpdateTextView.text = ""
				time < startTime -> timeSinceUpdateTextView.text = ""
				isRightNow(time) -> timeSinceUpdateTextView.setText(R.string.right_now)
				else             -> timeSinceUpdateTextView.text = formatRelativeTime(time)
			}
		}
	}

	private fun isRightNow(time: Instant): Boolean {
		val age = time until clock.now
		return age < updateTimeResolution
	}

	private fun formatRelativeTime(time: Instant): CharSequence {
		return DateUtils.getRelativeTimeSpanString(time.toEpochMilli(), clock.millis(), updateTimeResolution.toMillis())
	}

	companion object {
	    val startTime = ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant()!!
	}
}
