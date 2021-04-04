package se.gustavkarlsson.skylight.android.lib.settings

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.db.DbSettingsQueries

internal class SqlDelightSettings(
    private val queries: DbSettingsQueries,
    private val placesRepository: PlacesRepository,
    private val dispatcher: CoroutineDispatcher
) : Settings {

    override suspend fun setNotificationTriggerLevel(placeId: PlaceId, level: TriggerLevel) {
        val placeIdLong = placeId.value
        val exists = queries.getById(placeIdLong)
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .first() != null

        val levelIndex = level.ordinal.toLong()
        if (exists)
            queries.update(levelIndex, placeIdLong)
        else
            queries.insert(placeIdLong, levelIndex)
    }

    override fun clearNotificationTriggerLevel(placeId: PlaceId) = queries.delete(placeId.value)

    @ExperimentalCoroutinesApi
    override fun streamNotificationTriggerLevels(): Flow<Map<PlaceId, TriggerLevel>> =
        placesRepository.stream()
            .flatMapLatest { places ->
                getTriggerLevelRecords()
                    .map { records ->
                        places
                            .filterIsInstance<Place.Saved.Favorite>()
                            .map { favorite -> favorite.id to getTriggerLevel(favorite, records) }
                            .toMap()
                    }
            }

    private fun getTriggerLevelRecords(): Flow<List<TriggerLevelRecord>> =
        queries
            .selectAll { id, levelIndex -> TriggerLevelRecord(PlaceId.fromLong(id), levelIndex) }
            .asFlow()
            .mapToList()
}

private fun getTriggerLevel(
    place: Place,
    records: List<TriggerLevelRecord>
): TriggerLevel {
    val matchingRecord = records.find { record -> record.placeId == place.id }
        ?: return Settings.DEFAULT_TRIGGER_LEVEL
    return findTriggerLevelByIndex(matchingRecord.triggerLevelIndex)
        ?: Settings.DEFAULT_TRIGGER_LEVEL
}

private fun findTriggerLevelByIndex(levelIndex: Long): TriggerLevel? =
    if (levelIndex in TriggerLevel.values().indices)
        TriggerLevel.values()[levelIndex.toInt()]
    else
        null

private data class TriggerLevelRecord(val placeId: PlaceId, val triggerLevelIndex: Long)
