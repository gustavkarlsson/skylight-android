package se.gustavkarlsson.skylight.android.lib.runversion

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.VersionCode

@Module
@ContributesTo(AppScopeMarker::class)
object LibRunVersionModule {

    @Provides
    @Reusable
    internal fun runVersionManager(context: Context, @VersionCode currentVersionCode: Int): RunVersionManager {
        val dataStore = context.dataStore
        return DataStoreRunVersionManager(dataStore, currentVersionCode)
    }
}

private const val PREFS_FILE_NAME = "run_version"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFS_FILE_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, PREFS_FILE_NAME))
    },
)
