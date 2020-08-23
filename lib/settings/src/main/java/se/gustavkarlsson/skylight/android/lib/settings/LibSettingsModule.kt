package se.gustavkarlsson.skylight.android.lib.settings

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository

@Module
object LibSettingsModule {

    @Provides
    @AppScope
    internal fun settings(
        context: Context,
        placesRepository: PlacesRepository,
        @Io dispatcher: CoroutineDispatcher
    ): Settings {
        val driver = AndroidSqliteDriver(Database.Schema, context, "settings.db")
        val database = Database(driver)
        return SqlDelightSettings(
            queries = database.dbSettingsQueries,
            placesRepository = placesRepository,
            dispatcher = dispatcher
        )
    }
}
