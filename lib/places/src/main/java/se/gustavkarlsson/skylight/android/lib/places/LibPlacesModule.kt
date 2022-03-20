package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.time.Time

@Module
@ContributesTo(AppScopeMarker::class)
object LibPlacesModule {

    @Provides
    @Reusable
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
    @Reusable
    internal fun placeSelectionStorage(
        context: Context,
        placesRepository: PlacesRepository,
    ): SelectedPlaceRepository {
        val datastore = context.dataStore
        return PlacesRepoSelectedPlaceRepository(placesRepository, datastore)
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "selected_place")
