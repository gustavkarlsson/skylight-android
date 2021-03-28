package se.gustavkarlsson.skylight.android.lib.places

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlaceQueries
import se.gustavkarlsson.skylight.android.lib.time.Time

// FIXME clean up repetition in mapping
internal class SqlDelightPlacesRepository(
    private val queries: DbPlaceQueries,
    @Io private val dispatcher: CoroutineDispatcher,
    private val time: Time,
    private val maxRecentCount: Int,
) : PlacesRepository {

    override suspend fun setFavorite(placeId: Long): Place.Favorite = withContext(dispatcher) {
        val now = time.now()
        queries.updateType(TYPE_FAVORITE, now.toEpochMilli(), placeId)
        queries.selectById(placeId) { id, name, latitude, longitude, _, lastChangedMillis ->
            Place.Favorite(
                id = id,
                nameString = name,
                location = Location(latitude, longitude),
                lastChanged = Instant.ofEpochMilli(lastChangedMillis),
            )
        }.exactlyOne()
    }

    override suspend fun setRecent(placeId: Long): Place.Recent = withContext(dispatcher) {
        val now = time.now()
        queries.updateType(TYPE_RECENT, now.toEpochMilli(), placeId)
        val updated = queries.selectById(placeId) { id, name, latitude, longitude, _, lastChangedMillis ->
            Place.Recent(
                id = id,
                nameString = name,
                location = Location(latitude, longitude),
                lastChanged = Instant.ofEpochMilli(lastChangedMillis),
            )
        }.exactlyOne()
        removeOldRecents()
        updated
    }

    override suspend fun addRecent(name: String, location: Location): Place.Recent = withContext(dispatcher) {
        val now = time.now()
        queries.insertRecent(name, location.latitude, location.longitude, now.toEpochMilli())
        val inserted = queries
            .selectLastInserted { id, theName, latitude, longitude, _, lastChangedMillis ->
                Place.Recent(
                    id = id,
                    nameString = theName,
                    location = Location(latitude, longitude),
                    lastChanged = Instant.ofEpochMilli(lastChangedMillis),
                )
            }
            .exactlyOne()
        removeOldRecents()
        inserted
    }

    private suspend fun <T : Place> Query<T>.exactlyOne(): T {
        return asFlow()
            .mapToOne(dispatcher)
            .first()
    }

    private fun removeOldRecents() = queries.keepMostRecent(maxRecentCount.toLong())

    override fun stream(): Flow<List<Place>> =
        queries
            .selectAll { id, name, latitude, longitude, type, lastChangedMillis ->
                createPlace(type, id, name, latitude, longitude, lastChangedMillis)
            }
            .asFlow()
            .mapToList(dispatcher)
            .map { listOf(Place.Current) + it }
            .distinctUntilChanged()
}

private fun createPlace(
    type: String,
    id: Long,
    name: String,
    latitude: Double,
    longitude: Double,
    lastChangedMillis: Long,
): Place {
    val location = Location(latitude, longitude)
    val lastChanged = Instant.ofEpochMilli(lastChangedMillis)
    return when (type) {
        TYPE_FAVORITE -> Place.Favorite(id, name, location, lastChanged)
        TYPE_RECENT -> Place.Recent(id, name, location, lastChanged)
        else -> error("Unexpected location type: $type")
    }
}

private const val TYPE_FAVORITE = "favorite"
private const val TYPE_RECENT = "recent"
