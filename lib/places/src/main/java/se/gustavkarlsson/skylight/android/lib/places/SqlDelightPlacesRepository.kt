package se.gustavkarlsson.skylight.android.lib.places

import com.ioki.textref.TextRef
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.utils.allowDiskWritesInStrictMode
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlaceQueries

internal class SqlDelightPlacesRepository(
    private val queries: DbPlaceQueries,
    @Io private val dispatcher: CoroutineDispatcher
) : PlacesRepository {

    override fun add(name: String, location: Location) = allowDiskWritesInStrictMode {
        // TODO This should not run on the main thread
        queries.insert(name, location.latitude, location.longitude)
    }

    override fun remove(placeId: Long) = allowDiskWritesInStrictMode {
        // TODO This should not run on the main thread
        queries.delete(placeId)
    }

    override fun stream(): Flow<List<Place>> =
        queries
            .selectAll { id, name, latitude, longitude ->
                Place.custom(id, TextRef.string(name), Location(latitude, longitude))
            }
            .asFlow()
            .mapToList(dispatcher)
            .map { listOf(Place.Current) + it }
            .distinctUntilChanged()
}
