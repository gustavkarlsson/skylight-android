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

    override suspend fun setNotificationTriggerLevel(level: TriggerLevel) {
        val triggerLevelIdLong = level.id
        dataStore.updateData { message ->
            with(message.toBuilder()) {
                triggerLevelId = triggerLevelIdLong
                build()
            }
        }
    }

    override suspend fun setPlaceNotification(placeId: PlaceId, enabled: Boolean) {
        val placeIdLong = placeId.value
        dataStore.updateData { message ->
            with(message.toBuilder()) {
                if (enabled) {
                    putPlaceIdNotification(placeIdLong, true)
                } else {
                    removePlaceIdNotification(placeIdLong)
                }
                build()
            }
        }
    }

    // FIXME Remove place ID:s for places that do not exist
    override fun stream(): Flow<Settings> {
        return dataStore.data
            .map { message ->
                val notificationTriggerLevel = triggerLevelFromId(message.triggerLevelId)
                val placeIdsWithNotification = message.placeIdNotificationMap
                    .filterValues { it }
                    .mapKeys { (placeIdLong, _) ->
                        PlaceId.fromLong(placeIdLong)
                    }
                    .keys
                Settings(
                    notificationTriggerLevel = notificationTriggerLevel,
                    placeIdsWithNotification = placeIdsWithNotification,
                )
            }
    }
}

private fun triggerLevelFromId(id: Long): TriggerLevel = when (id) {
    0L -> DEFAULT_TRIGGER_LEVEL
    1L -> TriggerLevel.LOW
    2L -> TriggerLevel.MEDIUM
    3L -> TriggerLevel.HIGH
    else -> {
        logError { "Unsupported trigger level id: $id" }
        DEFAULT_TRIGGER_LEVEL
    }
}

private val DEFAULT_TRIGGER_LEVEL: TriggerLevel get() = TriggerLevel.MEDIUM

private val TriggerLevel.id: Long
    get() = when (this) {
        TriggerLevel.LOW -> 1
        TriggerLevel.MEDIUM -> 2
        TriggerLevel.HIGH -> 3
    }
