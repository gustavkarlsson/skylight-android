package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import se.gustavkarlsson.skylight.android.core.Io

internal class SharedPrefsPlaceSelectionStorage(
    context: Context,
    private val dispatcher: CoroutineDispatcher,
) : PlaceSelectionStorage {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
    }

    override fun saveIndex(index: Int) {
        sharedPreferences.edit {
            putInt(PLACE_INDEX_KEY, index)
        }
    }

    override suspend fun loadIndex(): Int? =
        withContext(dispatcher) {
            val index = sharedPreferences.getInt(PLACE_INDEX_KEY, NULL_VALUE)
            if (index == NULL_VALUE) null else index
        }
}

private const val PREFS_FILE_NAME = "selected_place"
private const val PLACE_INDEX_KEY = "place_index"
private const val NULL_VALUE = -1
