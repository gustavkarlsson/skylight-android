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
    private val newSettingsRepository: SettingsRepository,
    private val dispatcher: CoroutineDispatcher,
) : ModuleStarter {
    override suspend fun start() {
        migrateFromSqlDelightToDataStore()
    }

    private suspend fun migrateFromSqlDelightToDataStore() {
        val oldTriggerLevels = oldQueries
            .selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .first()
            .map { levelId ->
                triggerLevelFromId(levelId)
            }

        val lowestTriggerLevel = oldTriggerLevels.minOrNull() ?: return
        newSettingsRepository.setNotificationTriggerLevel(PlaceId.Current, lowestTriggerLevel)
        oldQueries.deleteAll()
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
