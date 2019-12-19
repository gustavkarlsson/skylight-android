package se.gustavkarlsson.skylight.android.lib.places

import de.halfbit.knot.knot
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository
import timber.log.Timber

internal class PlacesRepoSelectedPlaceRepository(
	placesRepo: PlacesRepository,
	placeSelectionStorage: PlaceSelectionStorage,
	disposables: CompositeDisposable
) : SelectedPlaceRepository {
	private val knot =
		createKnot(
			placesRepo.all.toObservable(),
			placeSelectionStorage::saveIndex,
			placeSelectionStorage::loadIndex
		).apply { disposables.add(this) }

	override fun set(place: Place) = knot.change.accept(Change.SelectionChanged(place))

	override fun get(): Place = knot.state
		.map(State::selected)
		.blockingFirst()

	override fun stream(): Observable<Place> = knot.state
		.map(State::selected)
		.distinctUntilChanged()
}

private fun createKnot(
	placesStream: Observable<List<Place>>,
	saveIndex: (Int) -> Unit,
	loadIndex: () -> Int
) = knot<State, Change, Nothing> {

	state {
		initial = State.Initial(loadIndex())

		watch<State.Loaded> {
			val index = it.places.indexOf(it.selected)
			saveIndex(index)
		}
	}

	events {
		source { placesStream.map(Change::PlacesUpdated) }
	}

	changes {
		reduce { change ->
			when (change) {
				is Change.SelectionChanged -> selectionChanged(change.place).only
				is Change.PlacesUpdated -> placesUpdated(change.places).only
			}
		}
	}
}

private fun State.selectionChanged(newSelection: Place): State =
	when (this) {
		is State.Initial -> {
			Timber.e(
				"Cannot select a place before loading places. Place: %s",
				newSelection
			)
			this
		}
		is State.Loaded -> {
			if (newSelection in places) {
				copy(selected = newSelection)
			} else {
				Timber.e(
					"Cannot select a place that is not loaded. Place: %s, Loaded: %s",
					newSelection,
					places
				)
				this
			}
		}
	}

private fun State.placesUpdated(newPlaces: List<Place>): State {
	val selected = when (this) {
		is State.Initial -> {
			val newIndex = selectedIndex.coerceIn(newPlaces.indices)
			newPlaces[newIndex]
		}
		is State.Loaded -> when {
			newPlaces.size > places.size -> {
				(newPlaces - places).first()
			}
			selected in newPlaces -> {
				selected
			}
			else -> {
				val newIndex = places.indexOf(selected).coerceIn(newPlaces.indices)
				newPlaces[newIndex]
			}
		}
	}
	return State.Loaded(selected = selected, places = newPlaces)
}

private sealed class State {
	abstract val selected: Place

	data class Initial(val selectedIndex: Int) : State() {
		override val selected = Place.Current
	}

	data class Loaded(override val selected: Place, val places: List<Place>) : State()
}

private sealed class Change {
	data class SelectionChanged(val place: Place) : Change()
	data class PlacesUpdated(val places: List<Place>) : Change()
}
