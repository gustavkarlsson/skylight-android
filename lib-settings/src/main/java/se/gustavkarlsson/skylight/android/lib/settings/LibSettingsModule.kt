package se.gustavkarlsson.skylight.android.lib.settings

import android.content.Context
import androidx.core.content.edit
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
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
