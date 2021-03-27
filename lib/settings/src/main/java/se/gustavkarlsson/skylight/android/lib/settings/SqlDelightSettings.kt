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
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.db.DbSettingsQueries

internal class SqlDelightSettings(
    private val queries: DbSettingsQueries,
    private val placesRepository: PlacesRepository,
    private val dispatcher: CoroutineDispatcher
) : Settings {

    override suspend fun setNotificationTriggerLevel(place: Place, level: TriggerLevel) {
        val placeId = place.getId()
        val exists = queries.getById(placeId)
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .first() != null

        val levelIndex = level.ordinal.toLong()
        if (exists)
            queries.update(levelIndex, placeId)
        else
            queries.insert(placeId, levelIndex)
    }

    override fun clearNotificationTriggerLevel(place: Place) = queries.delete(place.getId())

    // FIXME only take favorites
    @ExperimentalCoroutinesApi
    override fun streamNotificationTriggerLevels(): Flow<List<Pair<Place, TriggerLevel>>> =
        placesRepository.stream()
            .flatMapLatest { places ->
                getTriggerLevelRecords()
                    .map { records ->
                        places.map { place ->
                            place to getTriggerLevel(place, records)
                        }
                    }
            }

    private fun getTriggerLevelRecords(): Flow<List<TriggerLevelRecord>> =
        queries
            .selectAll { id, levelIndex -> TriggerLevelRecord(id, levelIndex) }
            .asFlow()
            .mapToList()
}

private fun getTriggerLevel(
    place: Place,
    records: List<TriggerLevelRecord>
): TriggerLevel {
    val placeId = place.getId()
    val matchingRecord = records.find { record -> record.placeId == placeId }
        ?: return Settings.DEFAULT_TRIGGER_LEVEL
    return findTriggerLevelByIndex(matchingRecord.triggerLevelIndex)
        ?: Settings.DEFAULT_TRIGGER_LEVEL
}

private fun Place.getId(): Long = id ?: CURRENT_ID

private fun findTriggerLevelByIndex(levelIndex: Long): TriggerLevel? =
    if (levelIndex in TriggerLevel.values().indices)
        TriggerLevel.values()[levelIndex.toInt()]
    else
        null

private data class TriggerLevelRecord(val placeId: Long, val triggerLevelIndex: Long)

private const val CURRENT_ID = -1L
