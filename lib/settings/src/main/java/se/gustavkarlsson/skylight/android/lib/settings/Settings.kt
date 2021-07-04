package se.gustavkarlsson.skylight.android.lib.settings

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId

interface Settings {
    suspend fun setNotificationTriggerLevel(placeId: PlaceId, level: TriggerLevel)

    fun streamNotificationTriggerLevels(): Flow<NotificationTriggerLevels>
}
