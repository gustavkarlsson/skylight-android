package se.gustavkarlsson.skylight.android.lib.places

import com.ioki.textref.TextRef
import com.jakewharton.rx.replayingShare
import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.lib.places.db.DbPlaceQueries
import se.gustavkarlsson.skylight.android.services.PlacesRepository

internal class SqlDelightPlacesRepository(
	private val queries: DbPlaceQueries,
	dbScheduler: Scheduler = Schedulers.io()
) : PlacesRepository {

	override fun add(name: String, location: Location) =
		queries.insert(name, location.latitude, location.longitude)

	override fun remove(placeId: Long) = queries.delete(placeId)

	private val stream =
		queries
			.selectAll<Place> { id, name, latitude, longitude ->
				Place.Custom(id, TextRef(name), Location(latitude, longitude))
			}
			.asObservable(dbScheduler)
			.mapToList()
			.map { listOf(Place.Current) + it }
			.distinctUntilChanged()
			.replayingShare()

	override fun stream(): Observable<List<Place>> = stream
}
