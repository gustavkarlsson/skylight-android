package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Place

interface Settings {
    val notificationTriggerLevels: Flowable<List<Pair<Place, ChanceLevel?>>>

	companion object {
		val DEFAULT_TRIGGER_LEVEL = ChanceLevel.MEDIUM
	}
}
