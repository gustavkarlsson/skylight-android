package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.get
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.valueOr
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

internal class StoreReverseGeocoder(
    private val store: Store<Location, Optional<String>>,
    private val retryDelay: Duration
) : ReverseGeocoder {

    override suspend fun get(locationResult: LocationResult): ReverseGeocodingResult {
        val result = when (locationResult) {
            is LocationResult.Success -> getName(locationResult.location)
            is LocationResult.Failure -> ReverseGeocodingResult.Failure.Location
        }
        logInfo { "Provided location name: $result" }
        return result
    }

    @ExperimentalCoroutinesApi
    override fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<ReverseGeocodingResult>> {
        return locations
            .flatMapLatest { loadableLocationResult ->
                when (loadableLocationResult) {
                    Loadable.Loading -> flowOf(Loadable.Loading)
                    is Loadable.Loaded -> when (val result = loadableLocationResult.value) {
                        is LocationResult.Success -> getNameWithRetry(result.location)
                        is LocationResult.Failure -> flowOf(Loadable.Loaded(ReverseGeocodingResult.Failure.Location))
                    }
                }
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed location name: $it" } }
    }

    private suspend fun getNameWithRetry(location: Location): Flow<Loadable<ReverseGeocodingResult>> = flow {
        emit(Loadable.Loading)
        do {
            val result = getName(location)
            emit(Loadable.loaded(result))
            val shouldRetry = result is ReverseGeocodingResult.Failure.Io
            if (shouldRetry) {
                delay(retryDelay.toMillis())
            }
        } while (shouldRetry)
    }

    private suspend fun getName(location: Location): ReverseGeocodingResult = try {
        store.get(location)
            .map { ReverseGeocodingResult.Success(it) }
            .valueOr { ReverseGeocodingResult.Failure.NotFound }
    } catch (e: IOException) {
        ReverseGeocodingResult.Failure.Io(e)
    }
}
