package se.gustavkarlsson.skylight.android.lib.settings

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place

interface Settings {
    suspend fun setNotificationTriggerLevel(place: Place, level: TriggerLevel)

    fun clearNotificationTriggerLevel(place: Place)

    fun streamNotificationTriggerLevels(): Flow<List<Pair<Place, TriggerLevel>>>

    companion object {
        val DEFAULT_TRIGGER_LEVEL = TriggerLevel.MEDIUM
    }
}
