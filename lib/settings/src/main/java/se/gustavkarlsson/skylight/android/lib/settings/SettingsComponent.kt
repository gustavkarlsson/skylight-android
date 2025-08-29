package se.gustavkarlsson.skylight.android.lib.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineDispatcher
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.settings.proto.SettingsMessage

@Component
abstract class SettingsComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
    @Component internal val placesComponent: PlacesComponent,
) {

    abstract val settingsRepository: SettingsRepository

    abstract val moduleStarter: ModuleStarter

    @Provides
    internal fun settings(
        context: Context,
        placesRepository: PlacesRepository,
    ): SettingsRepository {
        val dataStore = context.settingsDataStore
        return DataStoreSettingsRepository(dataStore, placesRepository)
    }

    @Provides
    internal fun moduleStarter(
        context: Context,
        @Io dispatcher: CoroutineDispatcher,
        settingsRepository: SettingsRepository,
    ): ModuleStarter {
        val driver = AndroidSqliteDriver(Database.Schema, context, "settings.db")
        val database = Database(driver)
        val queries = database.dbSettingsQueries
        return SettingsModuleStarter(queries, settingsRepository, dispatcher)
    }

    companion object {
        val instance: SettingsComponent = SettingsComponent::class.create(
            coreComponent = CoreComponent.instance,
            placesComponent = PlacesComponent.instance,
        )
    }
}

private val Context.settingsDataStore: DataStore<SettingsMessage> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer,
)
