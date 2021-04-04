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
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlace
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlaceQueries
import se.gustavkarlsson.skylight.android.lib.time.Time

internal class SqlDelightPlacesRepository(
    private val queries: DbPlaceQueries,
    @Io private val dispatcher: CoroutineDispatcher,
    private val time: Time,
    private val maxRecentsCount: Int,
) : PlacesRepository {

    override suspend fun setFavorite(place: Place.Saved): Place.Saved.Favorite = withContext(dispatcher) {
        val now = time.now()
        queries.updateType(TYPE_FAVORITE, now.toEpochMilli(), place.id)
        queries.selectById(place.id)
            .exactlyOne()
            .toPlace()
    }

    override suspend fun setRecent(place: Place.Saved): Place.Saved.Recent = withContext(dispatcher) {
        val now = time.now()
        queries.updateType(TYPE_RECENT, now.toEpochMilli(), place.id)
        val updated = queries.selectById(place.id)
            .exactlyOne()
            .toPlace<Place.Saved.Recent>()
        removeOldRecents()
        updated
    }

    override suspend fun addRecent(name: String, location: Location): Place.Saved.Recent = withContext(dispatcher) {
        val now = time.now()
        queries.insertRecent(name, location.latitude, location.longitude, now.toEpochMilli())
        val inserted = queries.selectLastInserted()
            .exactlyOne()
            .toPlace<Place.Saved.Recent>()
        removeOldRecents()
        inserted
    }

    private fun removeOldRecents() = queries.keepMostRecent(maxRecentsCount.toLong())

    override suspend fun updateLastChanged(place: Place.Saved): Place = withContext(dispatcher) {
        val now = time.now()
        queries.updateLastChanged(now.toEpochMilli(), place.id)
        queries.selectById(place.id)
            .exactlyOne()
            .toPlace()
    }

    private suspend fun <T : Any> Query<T>.exactlyOne(): T {
        return asFlow()
            .mapToOne(dispatcher)
            .first()
    }

    override fun stream(): Flow<List<Place>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { dbPlaces ->
                val places = dbPlaces.map { dbPlace ->
                    dbPlace.toPlace<Place>()
                }
                listOf(Place.Current) + places
            }
            .distinctUntilChanged()
}

private inline fun <reified T : Place> DbPlace.toPlace(): T {
    val location = Location(latitude, longitude)
    val lastChanged = Instant.ofEpochMilli(lastChangedMillis)
    val place = when (type) {
        TYPE_FAVORITE -> Place.Saved.Favorite(id, name, location, lastChanged)
        TYPE_RECENT -> Place.Saved.Recent(id, name, location, lastChanged)
        else -> error("Unexpected location type: $type")
    }
    return place as? T ?: error("Unexpected last inserted place type: ${place.javaClass.name}")
}

private const val TYPE_FAVORITE = "favorite"
private const val TYPE_RECENT = "recent"
