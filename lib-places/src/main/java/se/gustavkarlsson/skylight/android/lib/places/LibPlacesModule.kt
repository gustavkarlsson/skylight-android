package se.gustavkarlsson.skylight.android.lib.places

import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.dsl.module.module

val libPlacesModule = module {

	single<PlacesRepository> {
		val driver = AndroidSqliteDriver(Database.Schema, get(), "places.db")
		val database = Database(driver)
		SqlDelightPlacesRepository(database.dbPlaceQueries)
	}
}
