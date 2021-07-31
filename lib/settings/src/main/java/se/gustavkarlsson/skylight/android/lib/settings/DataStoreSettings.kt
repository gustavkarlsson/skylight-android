package se.gustavkarlsson.skylight.android.lib.settings

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.settings.proto.SettingsMessage

internal class DataStoreSettings(
    private val dataStore: DataStore<SettingsMessage>,
) : Settings {

    override suspend fun setNotificationTriggerLevel(placeId: PlaceId, level: TriggerLevel) {
        val levelId = level.id
        dataStore.updateData {
            SettingsMessage.newBuilder()
                .setTriggerLevelId(levelId)
                .build()
        }
    }

    override fun streamNotificationTriggerLevels(): Flow<NotificationTriggerLevels> {
        return dataStore.data
            .map { message ->
                val map = mapOf(
                    PlaceId.Current as PlaceId to triggerLevelFromId(message.triggerLevelId)
                )
                NotificationTriggerLevels(map)
            }
    }
}

private fun triggerLevelFromId(id: Long): TriggerLevel = when (id) {
    0L -> TriggerLevel.LOW
    1L -> TriggerLevel.MEDIUM
    2L -> TriggerLevel.HIGH
    3L -> TriggerLevel.NEVER
    else -> {
        logError { "Unsupported trigger level id: $id" }
        TriggerLevel.NEVER
    }
}

private val TriggerLevel.id: Long
    get() = when (this) {
        TriggerLevel.LOW -> 0
        TriggerLevel.MEDIUM -> 1
        TriggerLevel.HIGH -> 2
        TriggerLevel.NEVER -> 3
    }
