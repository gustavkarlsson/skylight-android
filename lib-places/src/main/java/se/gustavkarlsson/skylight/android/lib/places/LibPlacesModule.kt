package se.gustavkarlsson.skylight.android.lib.places

import android.annotation.SuppressLint
import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.reactivex.disposables.CompositeDisposable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository
import javax.inject.Singleton

val libPlacesModule = module {

    single<PlacesRepository> {
        val driver = AndroidSqliteDriver(Database.Schema, get(), "places.db")
        val database = Database(driver)
        SqlDelightPlacesRepository(database.dbPlaceQueries)
    }

    single<PlaceSelectionStorage> {
        SharedPrefsPlaceSelectionStorage(context = get())
    }

    single<SelectedPlaceRepository> {
        // TODO activity local composite disposable?
        PlacesRepoSelectedPlaceRepository(
            placesRepo = get(),
            placeSelectionStorage = get(),
            disposables = CompositeDisposable()
        )
    }

    single<ModuleStarter>("places") {
        val placesRepository = get<PlacesRepository>()
        val analytics = get<Analytics>()
        object : ModuleStarter {
            @SuppressLint("CheckResult")
            override fun start() {
                placesRepository.stream()
                    .map { it.count() }
                    .distinctUntilChanged()
                    .subscribe { placesCount ->
                        analytics.setProperty("places_count", placesCount)
                    }
            }
        }
    }
}

@Module
class LibPlacesModule {

    @Provides
    @Singleton
    internal fun placesRepository(context: Context): PlacesRepository {
        val driver = AndroidSqliteDriver(Database.Schema, context, "places.db")
        val database = Database(driver)
        return SqlDelightPlacesRepository(database.dbPlaceQueries)
    }

    @Provides
    @Singleton
    internal fun selectedPlaceRepository(
        context: Context,
        placesRepository: PlacesRepository
    ): SelectedPlaceRepository {
        val storage = SharedPrefsPlaceSelectionStorage(context)
        return PlacesRepoSelectedPlaceRepository(
            placesRepo = placesRepository,
            placeSelectionStorage = storage,
            disposables = CompositeDisposable()
        )
        // TODO activity local composite disposable?
    }

    @Provides
    @Singleton
    @IntoSet
    fun moduleStarter(placesRepository: PlacesRepository, analytics: Analytics): ModuleStarter =
        object : ModuleStarter {
            @SuppressLint("CheckResult")
            override fun start() {
                // TODO deal with disposable
                placesRepository.stream()
                    .map { it.count() }
                    .distinctUntilChanged()
                    .subscribe { placesCount ->
                        analytics.setProperty("places_count", placesCount)
                    }
            }
        }
}
