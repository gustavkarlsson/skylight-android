package se.gustavkarlsson.skylight.android.util

import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class CountdownTimer(
		duration: Duration,
		private val clock: Clock
) {
	private val expiryTime: Instant = clock.instant().plus(duration)

	val remainingTime: Duration
		get() {
			val now = clock.instant()
			return Duration.between(now, expiryTime)
		}

}
