package se.gustavkarlsson.skylight.android.lib.settings

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId

interface SettingsRepository {
    suspend fun setNotificationTriggerLevel(level: TriggerLevel)
    suspend fun setPlaceNotification(placeId: PlaceId, enabled: Boolean)
    fun stream(): Flow<Settings>
}
