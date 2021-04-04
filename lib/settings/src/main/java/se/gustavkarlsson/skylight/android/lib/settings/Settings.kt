package se.gustavkarlsson.skylight.android.lib.settings

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId

// FIXME use PlaceId:s instead
// FIXME auto sync with places?
// TODO add function for getting current?
interface Settings {
    suspend fun setNotificationTriggerLevel(placeId: PlaceId, level: TriggerLevel)

    fun clearNotificationTriggerLevel(placeId: PlaceId)

    fun streamNotificationTriggerLevels(): Flow<Map<PlaceId, TriggerLevel>>

    companion object {
        val DEFAULT_TRIGGER_LEVEL = TriggerLevel.MEDIUM
    }
}
