package se.gustavkarlsson.skylight.android.lib.settings

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun streamNotificationTriggerLevels(): Flow<Map<PlaceId, TriggerLevel>> =
        combine(placesRepository.stream(), streamAll()) { places, entries ->
            entries.removeZombies(places)
        }

    private fun streamAll(): Flow<Map<PlaceId, TriggerLevel>> {
        return queries
            .selectAll { id, levelIndex ->
                val placeId = PlaceId.fromLong(id)
                // FIXME don't use indices
                val triggerLevel = TriggerLevel.values().getOrNull(levelIndex.toInt()) ?: TriggerLevel.NEVER
                placeId to triggerLevel
            }
            .asFlow()
            .mapToList()
            .map { entries -> entries.toMap() }
    }

    private fun Map<PlaceId, TriggerLevel>.removeZombies(places: List<Place>): Map<PlaceId, TriggerLevel> {
        val livePlaceIds = places.map { place -> place.id }
        val deadPlaceIds = keys - livePlaceIds
        for (dead in deadPlaceIds) {
            queries.delete(dead.value)
        }
        return filterKeys { placeId -> placeId in livePlaceIds }
    }
}
