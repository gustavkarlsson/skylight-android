package se.gustavkarlsson.skylight.android.lib.settings

import com.jakewharton.rx.replayingShare
import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.db.DbSettingsQueries

internal class SqlDelightSettings(
    private val queries: DbSettingsQueries,
    private val placesRepository: PlacesRepository,
    private val dbScheduler: Scheduler = Schedulers.io()
) : Settings {

    override fun setNotificationTriggerLevel(place: Place, level: TriggerLevel): Completable {
        val placeId = place.getId()
        return queries.getById(placeId)
            .asObservable(dbScheduler)
            .mapToList()
            .firstElement()
            .map { it.isNotEmpty() }
            .switchIfEmpty(Single.just(false))
            .doOnSuccess { exists ->
                val levelIndex = level.ordinal.toLong()
                if (exists)
                    queries.update(levelIndex, placeId)
                else
                    queries.insert(placeId, levelIndex)
            }
            .ignoreElement()
    }

    override fun clearNotificationTriggerLevel(place: Place) = queries.delete(place.getId())

    override fun streamNotificationTriggerLevels(): Observable<List<Pair<Place, TriggerLevel>>> =
        placesRepository.stream()
            .switchMap { places ->
                getTriggerLevelRecords()
                    .map { records ->
                        places.map { place ->
                            place to getTriggerLevel(place, records)
                        }
                    }
            }
            .replayingShare()

    private fun getTriggerLevelRecords(): Observable<List<TriggerLevelRecord>> =
        queries
            .selectAll { id, levelIndex -> TriggerLevelRecord(id, levelIndex) }
            .asObservable(dbScheduler)
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

private fun Place.getId(): Long = when (this) {
    Place.Current -> CURRENT_ID
    is Place.Custom -> id
}

private fun findTriggerLevelByIndex(levelIndex: Long): TriggerLevel? =
    if (levelIndex in TriggerLevel.values().indices)
        TriggerLevel.values()[levelIndex.toInt()]
    else
        null

private data class TriggerLevelRecord(val placeId: Long, val triggerLevelIndex: Long)

private const val CURRENT_ID = -1L
