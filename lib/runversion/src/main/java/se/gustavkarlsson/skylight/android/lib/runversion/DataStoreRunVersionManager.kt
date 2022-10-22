package se.gustavkarlsson.skylight.android.lib.runversion

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import java.io.IOException

internal class DataStoreRunVersionManager(
    private val dataStore: DataStore<Preferences>,
    private val currentVersionCode: Int,
) : RunVersionManager {

    override suspend fun isFirstRun(): Boolean {
        return try {
            val prefs = dataStore.data.first()
            val lastVersion = prefs[LAST_RUN_VERSION_KEY]
            return lastVersion == null
        } catch (e: IOException) {
            logWarn(e) { "Failed read run version from shared prefs" }
            false
        }
    }

    override suspend fun signalRunCompleted() {
        try {
            dataStore.edit { prefs ->
                prefs[LAST_RUN_VERSION_KEY] = currentVersionCode
            }
            logDebug { "Signalled run completed" }
        } catch (e: IOException) {
            logWarn(e) { "Failed to signal run completed" }
        }
    }
}

private val LAST_RUN_VERSION_KEY = intPreferencesKey("last_run_version")
