package se.gustavkarlsson.skylight.android.lib.places

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineDispatcher
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Component
@PlacesScope
abstract class PlacesComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
    @Component internal val timeComponent: TimeComponent,
) {

    abstract val placesRepository: PlacesRepository

    abstract val selectedPlaceRepository: SelectedPlaceRepository

    @Provides
    @PlacesScope
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
    internal fun placeSelectionStorage(impl: PlacesRepoSelectedPlaceRepository): SelectedPlaceRepository = impl

    companion object {
        val instance: PlacesComponent = PlacesComponent::class.create(
            coreComponent = CoreComponent.instance,
            timeComponent = TimeComponent.instance,
        )
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class PlacesScope
