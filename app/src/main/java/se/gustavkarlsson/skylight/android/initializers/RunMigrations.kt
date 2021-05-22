package se.gustavkarlsson.skylight.android.initializers

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal fun Application.runMigrations() {
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            val sharedPrefs = getSharedPreferences("selected_place", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                remove("place_index")
            }
        }
    }
}
