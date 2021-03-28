package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class SharedPrefsPlaceSelectionStorage(
    context: Context,
    private val dispatcher: CoroutineDispatcher,
) : PlaceSelectionStorage {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
    }

    override fun saveId(id: Long) {
        sharedPreferences.edit {
            putLong(PLACE_ID_KEY, id)
        }
    }

    override suspend fun loadId(): Long =
        withContext(dispatcher) {
            sharedPreferences.getLong(PLACE_ID_KEY, Place.Current.id)
        }
}

private const val PREFS_FILE_NAME = "selected_place"
private const val PLACE_ID_KEY = "place_id"
