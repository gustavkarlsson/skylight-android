package se.gustavkarlsson.skylight.android.lib.runversion

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.VersionCode
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import java.io.IOException

@Inject
internal class DataStoreRunVersionManager(
    context: Context,
    @VersionCode private val currentVersionCode: Int,
) : RunVersionManager {

    private val dataStore = context.dataStore

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

private const val PREFS_FILE_NAME = "run_version"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFS_FILE_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, PREFS_FILE_NAME))
    },
)

private val LAST_RUN_VERSION_KEY = intPreferencesKey("last_run_version")
