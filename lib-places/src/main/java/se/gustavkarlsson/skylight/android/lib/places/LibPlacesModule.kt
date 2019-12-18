package se.gustavkarlsson.skylight.android.lib.places

import android.annotation.SuppressLint
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.reactivex.disposables.CompositeDisposable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository

val libPlacesModule = module {

	single<PlacesRepository> {
		val driver = AndroidSqliteDriver(Database.Schema, get(), "places.db")
		val database = Database(driver)
		SqlDelightPlacesRepository(database.dbPlaceQueries)
	}

	single<SelectedPlaceRepository> {
		// TODO activity local composite disposable?
		PlacesRepoSelectedPlaceRepository(get(), CompositeDisposable())
	}

	single<ModuleStarter>("places") {
		val placesRepository = get<PlacesRepository>()
		val analytics = get<Analytics>()
		object : ModuleStarter {
			@SuppressLint("CheckResult")
			override fun start() {
				placesRepository
					.all
					.map { it.count() }
					.distinctUntilChanged()
					.subscribe { placesCount ->
						analytics.setProperty("places_count", placesCount)
					}
			}
		}
	}
}
