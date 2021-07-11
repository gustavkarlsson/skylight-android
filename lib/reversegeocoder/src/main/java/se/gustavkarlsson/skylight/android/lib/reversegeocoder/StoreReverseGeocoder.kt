package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.left
import arrow.core.rightIfNotNull
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationError
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.location.approximate
import java.io.IOException

internal class StoreReverseGeocoder(
    private val store: Store<ApproximatedLocation, Option<String>>,
    private val retryDelay: Duration,
    private val approximationMeters: Double,
) : ReverseGeocoder {

    override suspend fun get(locationResult: LocationResult): ReverseGeocodingResult {
        val result = locationResult
            .mapLeft { error ->
                when (error) {
                    LocationError.NoPermission -> ReverseGeocodingError.NoLocationPermission
                    LocationError.Unknown -> ReverseGeocodingError.NoLocation
                }
            }
            .flatMap { location ->
                getName(location)
            }
        logInfo { "Provided location name: $result" }
        return result
    }

    // TODO Is there an operation that does this better?
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<ReverseGeocodingResult>> {
        return locations
            .flatMapLatest { loadableLocationResult ->
                loadableLocationResult.fold(
                    ifEmpty = { flowOf(Loading) },
                    ifSome = { locationResult ->
                        locationResult.fold(
                            ifLeft = { locationError ->
                                val error = when (locationError) {
                                    LocationError.NoPermission -> ReverseGeocodingError.NoLocationPermission
                                    LocationError.Unknown -> ReverseGeocodingError.NoLocation
                                }
                                flowOf(Loaded(error.left()))
                            },
                            ifRight = { location ->
                                getNameWithRetry(location)
                            }
                        )
                    }
                )
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed location name: $it" } }
    }

    private suspend fun getNameWithRetry(location: Location): Flow<Loadable<ReverseGeocodingResult>> = flow {
        emit(Loading)
        do {
            val result = getName(location)
            emit(Loaded(result))
            val shouldRetry = result.fold(
                ifLeft = { error -> error == ReverseGeocodingError.Io },
                ifRight = { false }
            )
            if (shouldRetry) {
                delay(retryDelay.toMillis())
            }
        } while (shouldRetry)
    }

    private suspend fun getName(location: Location): ReverseGeocodingResult = Either
        .catch {
            store.get(location.approximate(approximationMeters)).orNull()
        }
        .mapLeft { throwable ->
            when (throwable) {
                is IOException -> ReverseGeocodingError.Io
                else -> ReverseGeocodingError.Unknown
            }
        }
        .flatMap { name ->
            name.rightIfNotNull { ReverseGeocodingError.NotFound }
        }
}
