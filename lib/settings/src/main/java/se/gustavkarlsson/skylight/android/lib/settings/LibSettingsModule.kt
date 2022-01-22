package se.gustavkarlsson.skylight.android.lib.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.proto.SettingsMessage

@Module
@ContributesTo(AppScopeMarker::class)
object LibSettingsModule {

    @Provides
    @AppScope
    internal fun settings(
        context: Context,
        placesRepository: PlacesRepository,
    ): SettingsRepository {
        val dataStore = context.settingsDataStore
        return DataStoreSettingsRepository(dataStore, placesRepository)
    }

    @Provides
    @IntoSet
    fun moduleStarter(
        context: Context,
        @Io dispatcher: CoroutineDispatcher,
        settingsRepository: SettingsRepository,
    ): ModuleStarter {
        val driver = AndroidSqliteDriver(Database.Schema, context, "settings.db")
        val database = Database(driver)
        val queries = database.dbSettingsQueries
        return SettingsModuleStarter(queries, settingsRepository, dispatcher)
    }
}

private val Context.settingsDataStore: DataStore<SettingsMessage> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer,
)
