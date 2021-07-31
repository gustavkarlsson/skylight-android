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
