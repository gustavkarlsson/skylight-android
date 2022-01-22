package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.ModuleStarter
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
        return SqlDelightPlacesRepository(database.dbPlaceQueries, dispatcher, time)
    }

    @Provides
    internal fun placeSelectionStorage(impl: SharedPrefsPlaceSelectionStorage): PlaceSelectionStorage = impl

    @Provides
    internal fun selectedPlaceRepository(impl: PlacesRepoSelectedPlaceRepository): SelectedPlaceRepository = impl

    @Provides
    @IntoSet
    internal fun moduleStarter(impl: PlacesModuleStarter): ModuleStarter = impl
}
