package se.gustavkarlsson.skylight.android.lib.settings

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId

interface SettingsRepository {
    suspend fun setNotificationTriggerLevel(placeId: PlaceId, level: TriggerLevel)

    fun stream(): Flow<Settings>
}
