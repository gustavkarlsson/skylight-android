package se.gustavkarlsson.skylight.android.lib.places

import arrow.core.NonEmptyList
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
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlace
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlaceQueries
import se.gustavkarlsson.skylight.android.lib.time.Time

internal class SqlDelightPlacesRepository(
    private val queries: DbPlaceQueries,
    private val dispatcher: CoroutineDispatcher,
    private val time: Time,
) : PlacesRepository {

    // TODO Don't insert duplicates
    override suspend fun insert(name: String, location: Location): Place.Saved = withContext(dispatcher) {
        val now = time.now()
        queries.insert(name, location.latitude, location.longitude, now.toEpochMilli())
        queries.selectLastInserted()
            .exactlyOne()
            .toPlace()
    }

    override suspend fun delete(id: PlaceId.Saved): Boolean {
        queries.deleteById(id.value)
        val changedRows = queries.selectChangedCount().exactlyOne()
        return changedRows > 0
    }

    override suspend fun updateLastChanged(placeId: PlaceId.Saved): Place.Saved = withContext(dispatcher) {
        val now = time.now()
        queries.updateLastChanged(now.toEpochMilli(), placeId.value)
        queries.selectById(placeId.value)
            .exactlyOne()
            .toPlace()
    }

    private suspend fun <T : Any> Query<T>.exactlyOne(): T {
        return asFlow()
            .mapToOne(dispatcher)
            .first()
    }

    override fun stream(): Flow<NonEmptyList<Place>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { dbPlaces ->
                val places = dbPlaces.map { dbPlace ->
                    dbPlace.toPlace()
                }
                NonEmptyList(Place.Current, places)
            }
            .distinctUntilChanged()
}

private fun DbPlace.toPlace(): Place.Saved {
    val placeId = PlaceId.Saved(id)
    val location = Location(latitude, longitude)
    val lastChanged = Instant.ofEpochMilli(lastChangedMillis)
    return Place.Saved(placeId, name, location, lastChanged)
}
