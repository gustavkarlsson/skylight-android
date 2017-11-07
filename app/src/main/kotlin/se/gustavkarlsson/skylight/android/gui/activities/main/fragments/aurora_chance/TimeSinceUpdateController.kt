package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.text.format.DateUtils
import android.view.View
import android.view.View.VISIBLE
import android.widget.TextView
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.Instant.EPOCH
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.until

class TimeSinceUpdateController( // TODO Replace with custom view
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
				time == null     -> {
					timeSinceUpdateTextView.visibility = View.INVISIBLE
				}
				time <= EPOCH    -> {
					timeSinceUpdateTextView.visibility = View.INVISIBLE
				}
				isRightNow(time) -> {
					timeSinceUpdateTextView.visibility = VISIBLE
					timeSinceUpdateTextView.setText(R.string.right_now)
				}
				else             -> {
					timeSinceUpdateTextView.visibility = VISIBLE
					timeSinceUpdateTextView.text = formatRelativeTime(time)
				}
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
}
