package se.gustavkarlsson.skylight.android.lib.places

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal class PlacesRepoSelectedPlaceRepository(
    placesRepo: PlacesRepository,
    private val selectedIdDatastore: DataStore<Preferences>,
) : SelectedPlaceRepository {

    override suspend fun set(placeId: PlaceId) {
        selectedIdDatastore.edit { prefs ->
            prefs[PLACE_ID_KEY] = placeId.value
        }
    }

    private val selectedIdFlow = selectedIdDatastore.data.map { prefs ->
        val existingIdLong = prefs[PLACE_ID_KEY]
        if (existingIdLong != null) {
            PlaceId.fromLong(existingIdLong)
        } else {
            PlaceId.Current
        }
    }

    private val stream = combine(placesRepo.stream(), selectedIdFlow) { places, selectedId ->
        selectBestPlace(places, selectedId)
    }

    override fun stream(): Flow<Place> = stream
}

private fun selectBestPlace(places: NonEmptyList<Place>, selectedId: PlaceId): Place {
    return places.firstOrNull { place ->
        place.id == selectedId
    } ?: places.first()
}

private val PLACE_ID_KEY = longPreferencesKey("place_id")
