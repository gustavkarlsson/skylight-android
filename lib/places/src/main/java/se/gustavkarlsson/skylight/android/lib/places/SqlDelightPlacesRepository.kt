package se.gustavkarlsson.skylight.android.lib.places

import arrow.core.NonEmptyList
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlace
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlaceQueries
import se.gustavkarlsson.skylight.android.lib.time.Time
import kotlin.time.Instant

internal class SqlDelightPlacesRepository(
    private val queries: DbPlaceQueries,
    private val dispatcher: CoroutineDispatcher,
    private val time: Time,
) : PlacesRepository {

    override suspend fun insert(name: String, location: Location): Place.Saved = withContext(dispatcher) {
        val now = time.now()
        queries.transactionWithResult {
            val existing = queries.selectAll().executeAsList()
                .map(DbPlace::toPlace)
                .find { it.name == name && it.location == location }
            if (existing != null) {
                queries.updateLastChanged(now.toEpochMilliseconds(), existing.id.value)
                queries.selectById(existing.id.value).executeAsOne()
            } else {
                queries.insert(name, location.latitude, location.longitude, now.toEpochMilliseconds())
                queries.selectLastInserted().executeAsOne()
            }
        }.toPlace()
    }

    override suspend fun delete(id: PlaceId.Saved): Boolean {
        val changedRows = queries.transactionWithResult {
            queries.deleteById(id.value)
            queries.selectChangedCount().executeAsOne()
        }
        return changedRows > 0
    }

    override suspend fun updateLastChanged(placeId: PlaceId.Saved): Place.Saved = withContext(dispatcher) {
        val now = time.now()
        queries.transactionWithResult {
            queries.updateLastChanged(now.toEpochMilliseconds(), placeId.value)
            queries.selectById(placeId.value).executeAsOne()
        }.toPlace()
    }

    override fun stream(): Flow<NonEmptyList<Place>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { dbPlaces ->
                val places = dbPlaces.map(DbPlace::toPlace)
                NonEmptyList(Place.Current, places)
            }
            .distinctUntilChanged()
}

private fun DbPlace.toPlace(): Place.Saved {
    val placeId = PlaceId.Saved(id)
    val location = Location(latitude, longitude)
    val lastChanged = Instant.fromEpochMilliseconds(lastChangedMillis)
    return Place.Saved(placeId, name, location, lastChanged)
}
