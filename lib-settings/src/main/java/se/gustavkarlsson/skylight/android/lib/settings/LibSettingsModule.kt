package se.gustavkarlsson.skylight.android.lib.settings

import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.Settings

val libSettingsModule = module {

    single<DevelopSettings> {
        SqlDelightDevelopSettings()
    }

    single<Settings> {
        val driver = AndroidSqliteDriver(Database.Schema, get(), "settings.db")
        val database = Database(driver)
        SqlDelightSettings(
            queries = database.dbSettingsQueries,
            placesRepository = get()
        )
    }
}
