package se.gustavkarlsson.skylight.android.util

import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.extensions.until

internal class CountdownTimer(
		duration: Duration,
		private val clock: Clock
) {
	private val expiryTime = clock.instant().plus(duration)

	val remainingTime: Duration
		get() {
			val now = clock.instant()
			return now.until(expiryTime)
		}

}
