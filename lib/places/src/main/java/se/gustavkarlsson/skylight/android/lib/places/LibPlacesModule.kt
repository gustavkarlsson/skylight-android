package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.lib.time.Time

@Module
object LibPlacesModule {

    @Provides
    @AppScope
    internal fun placesRepository(
        context: Context,
        @Io dispatcher: CoroutineDispatcher,
        time: Time,
    ): PlacesRepository {
        val driver = AndroidSqliteDriver(Database.Schema, context, "places.db")
        val database = Database(driver)
        return SqlDelightPlacesRepository(database.dbPlaceQueries, dispatcher, time, maxRecentsCount = 5)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Provides
    @AppScope
    internal fun selectedPlaceRepository(
        context: Context,
        placesRepository: PlacesRepository,
        scope: CoroutineScope,
        @Io dispatcher: CoroutineDispatcher,
    ): SelectedPlaceRepository {
        val storage = SharedPrefsPlaceSelectionStorage(context, dispatcher)
        return PlacesRepoSelectedPlaceRepository(
            placesRepo = placesRepository,
            placeSelectionStorage = storage,
            scope = scope,
        )
    }

    @Provides
    @AppScope
    @IntoSet
    fun moduleStarter(placesRepository: PlacesRepository, analytics: Analytics): ModuleStarter =
        ModuleStarter { scope ->
            scope.launch {
                placesRepository.stream()
                    .map { it.count() }
                    .distinctUntilChanged()
                    .collect { placesCount ->
                        analytics.setProperty("places_count", placesCount)
                    }
            }
        }
}
