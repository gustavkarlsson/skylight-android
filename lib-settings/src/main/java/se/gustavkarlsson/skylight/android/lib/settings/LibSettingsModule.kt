package se.gustavkarlsson.skylight.android.lib.settings

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import javax.inject.Singleton

@Module
class LibSettingsModule {

    @Provides
    @Singleton
    internal fun settings(context: Context, placesRepository: PlacesRepository): Settings {
        val driver = AndroidSqliteDriver(Database.Schema, context, "settings.db")
        val database = Database(driver)
        return SqlDelightSettings(
            queries = database.dbSettingsQueries,
            placesRepository = placesRepository
        )
    }
}
