package se.gustavkarlsson.skylight.android.lib.settings

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.core.logging.logError
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
                PlaceId.fromLong(placeId) to triggerLevelFromId(levelId)
            }
            .asFlow()
            .mapToList(dispatcher)
            .first()
            .toMap()

    private suspend fun migrateNotificationTriggerLevel(oldSettings: Map<PlaceId, TriggerLevel>) {
        val lowestTriggerLevel = oldSettings.values.minOrNull()
        if (lowestTriggerLevel != null) {
            settingsRepository.setNotificationTriggerLevel(lowestTriggerLevel)
        }
    }

    private suspend fun migratePlacesWithNotification(oldSettings: Map<PlaceId, TriggerLevel>) {
        val placeIdsWithNotification = oldSettings
            .filterValues { triggerLevel ->
                triggerLevel != TriggerLevel.NEVER
            }
            .keys
        for (placeId in placeIdsWithNotification) {
            settingsRepository.setPlaceNotification(placeId, true)
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
