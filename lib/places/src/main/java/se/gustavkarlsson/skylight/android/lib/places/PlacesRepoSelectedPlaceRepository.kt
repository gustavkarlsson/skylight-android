package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
internal class PlacesRepoSelectedPlaceRepository(
    placesRepo: PlacesRepository,
    context: Context,
) : SelectedPlaceRepository {

    private val selectedIdDatastore = context.dataStore

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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "selected_place")

private val PLACE_ID_KEY = longPreferencesKey("place_id")
