package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

internal class SharedPrefsPlaceSelectionStorage(context: Context) : PlaceSelectionStorage {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
    }

    override fun saveIndex(index: Int) {
        sharedPreferences.edit {
            putInt(PLACE_INDEX_KEY, index)
        }
    }

    override fun loadIndex(): Int? {
        val index = sharedPreferences.getInt(PLACE_INDEX_KEY, -1)
        return if (index == DEFAULT_VALUE) null else index
    }
}

private const val PREFS_FILE_NAME = "selected_place"
private const val PLACE_INDEX_KEY = "place_index"
private const val DEFAULT_VALUE = -1
