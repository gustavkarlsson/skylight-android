package se.gustavkarlsson.skylight.android.initializers

import android.app.Application
import android.content.Context
import androidx.core.content.edit

internal fun Application.runMigrations() {
    val sharedPrefs = getSharedPreferences("selected_place", Context.MODE_PRIVATE)
    sharedPrefs.edit {
        remove("place_index")
    }
}
