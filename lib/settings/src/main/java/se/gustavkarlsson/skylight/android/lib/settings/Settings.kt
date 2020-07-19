package se.gustavkarlsson.skylight.android.lib.settings

import io.reactivex.Completable
import io.reactivex.Observable
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place

interface Settings {
    fun setNotificationTriggerLevel(place: Place, level: TriggerLevel): Completable

    fun clearNotificationTriggerLevel(place: Place)

    fun streamNotificationTriggerLevels(): Observable<List<Pair<Place, TriggerLevel>>>

    companion object {
        val DEFAULT_TRIGGER_LEVEL = TriggerLevel.MEDIUM
    }
}
