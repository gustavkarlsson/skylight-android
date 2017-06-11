package se.gustavkarlsson.skylight.android.util

import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.until

class CountdownTimer(
		duration: Duration,
		private val clock: Clock
) {
	private val expiryTime = clock.now + duration

	val remainingTime: Duration
		get() = clock.now until expiryTime

}
