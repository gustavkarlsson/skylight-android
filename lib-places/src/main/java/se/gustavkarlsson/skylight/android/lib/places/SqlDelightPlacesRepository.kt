package se.gustavkarlsson.skylight.android.lib.places

import com.ioki.textref.TextRef
import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlaceQueries

internal class SqlDelightPlacesRepository(
	private val queries: DbPlaceQueries
) : PlacesRepository {

	override fun add(name: String, location: Location) {
		queries.insert(name, location.latitude, location.longitude)
	}

	override fun remove(placeId: Long) {
		queries.delete(placeId)
	}

	override fun all(): Flowable<List<Place>> =
		queries
			.selectAll<Place> { id, name, latitude, longitude ->
				Place.Custom(id, TextRef(name), Location(latitude, longitude))
			}
			.asObservable()
			.mapToList()
			.map { listOf(Place.Current) + it }
			.toFlowable(BackpressureStrategy.LATEST)
}
