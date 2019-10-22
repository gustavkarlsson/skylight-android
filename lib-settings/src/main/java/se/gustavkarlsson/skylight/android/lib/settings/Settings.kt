package se.gustavkarlsson.skylight.android.lib.settings

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.lib.places.Place

interface Settings {
    val notificationTriggerLevels: Flowable<List<Pair<Place, ChanceLevel?>>>

	companion object {
		val DEFAULT_TRIGGER_LEVEL = ChanceLevel.MEDIUM
	}
}
