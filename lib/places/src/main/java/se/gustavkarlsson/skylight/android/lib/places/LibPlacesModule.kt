package se.gustavkarlsson.skylight.android.lib.places

import android.annotation.SuppressLint
import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.reactivex.disposables.CompositeDisposable
import se.gustavkarlsson.skylight.android.AppScope
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics

@Module
object LibPlacesModule {

    @Provides
    @AppScope
    internal fun placesRepository(context: Context): PlacesRepository {
        val driver = AndroidSqliteDriver(Database.Schema, context, "places.db")
        val database = Database(driver)
        return SqlDelightPlacesRepository(database.dbPlaceQueries)
    }

    @Provides
    @AppScope
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
    @AppScope
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
