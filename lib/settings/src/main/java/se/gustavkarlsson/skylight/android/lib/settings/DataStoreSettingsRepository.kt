package se.gustavkarlsson.skylight.android.lib.settings

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.settings.proto.SettingsMessage

internal class DataStoreSettingsRepository(
    private val dataStore: DataStore<SettingsMessage>,
) : SettingsRepository {

    override suspend fun setNotificationTriggerLevel(placeId: PlaceId, level: TriggerLevel) {
        val levelIdLong = level.id
        val placeIdLong = placeId.value
        dataStore.updateData { message ->
            with(message.toBuilder()) {
                triggerLevelId = levelIdLong
                if (level == TriggerLevel.NEVER) {
                    removePlaceIdNotification(placeIdLong)
                } else {
                    putPlaceIdNotification(placeIdLong, true)
                }
                build()
            }
        }
    }

    override fun stream(): Flow<Settings> {
        return dataStore.data
            .map { message ->
                val storedTriggerLevel = triggerLevelFromId(message.triggerLevelId)
                val map = message.placeIdNotificationMap
                    .entries.associate { (placeIdLong, enable) ->
                        val placeId = PlaceId.fromLong(placeIdLong)
                        val triggerLevel = if (enable) {
                            storedTriggerLevel
                        } else TriggerLevel.NEVER
                        placeId to triggerLevel
                    }
                Settings(map)
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
