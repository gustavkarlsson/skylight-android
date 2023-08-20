package se.gustavkarlsson.skylight.android.lib.settings

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.settings.db.DbSettingsQueries

internal class SettingsModuleStarter(
    private val oldQueries: DbSettingsQueries,
    private val settingsRepository: SettingsRepository,
    private val dispatcher: CoroutineDispatcher,
) : ModuleStarter {
    override suspend fun start() {
        migrateFromSqlDelightToDataStore()
    }

    private suspend fun migrateFromSqlDelightToDataStore() {
        val oldSettings = getOldSettings()
        migrateNotificationTriggerLevel(oldSettings)
        migratePlacesWithNotification(oldSettings)
        oldQueries.deleteAll()
    }

    private suspend fun getOldSettings() =
        oldQueries
            .selectAll { placeId, levelId ->
                PlaceId.fromLong(placeId) to triggerLevelFromOldId(levelId)
            }
            .asFlow()
            .mapToList(dispatcher)
            .first()
            .mapNotNull { (placeId, triggerLevel) ->
                if (triggerLevel != null) {
                    placeId to triggerLevel
                } else {
                    null
                }
            }
            .toMap()

    private suspend fun migrateNotificationTriggerLevel(oldSettings: Map<PlaceId, TriggerLevel>) {
        oldSettings.values.minOrNull()?.let { lowestTriggerLevel ->
            settingsRepository.setNotificationTriggerLevel(lowestTriggerLevel)
        }
    }

    private suspend fun migratePlacesWithNotification(oldSettings: Map<PlaceId, TriggerLevel>) {
        oldSettings.keys.forEach { placeId ->
            settingsRepository.setPlaceNotification(placeId, true)
        }
    }
}

private fun triggerLevelFromOldId(id: Long) = when (id) {
    0L -> TriggerLevel.LOW
    1L -> TriggerLevel.MEDIUM
    2L -> TriggerLevel.HIGH
    else -> null
}
