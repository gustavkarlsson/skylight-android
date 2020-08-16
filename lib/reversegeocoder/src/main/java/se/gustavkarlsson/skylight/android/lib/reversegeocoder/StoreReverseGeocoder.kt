package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.fresh
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
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.valueOr
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import java.io.IOException

internal class StoreReverseGeocoder(
    private val store: Store<Location, Optional<String>>,
    private val retryDelay: Duration
) : ReverseGeocoder {

    override suspend fun get(location: LocationResult, fresh: Boolean): ReverseGeocodingResult {
        val result = getSingleName(location) {
            if (fresh) {
                fresh(it)
            } else {
                get(it)
            }
        }
        logInfo { "Provided location name: $result" }
        return result
    }

    @ExperimentalCoroutinesApi
    override fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<ReverseGeocodingResult>> =
        locations
            .flatMapLatest { loadableLocationResult ->
                when (loadableLocationResult) {
                    Loadable.Loading -> flowOf(Loadable.Loading)
                    is Loadable.Loaded -> getSingleNameWithRetry(loadableLocationResult.value)
                }
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed location name: $it" } }

    private suspend fun getSingleNameWithRetry(
        locationResult: LocationResult
    ): Flow<Loadable<ReverseGeocodingResult>> =
        flow {
            emit(Loadable.loading())
            do {
                val result = getSingleName(locationResult) { get(it) }
                emit(Loadable.loaded(result))
                val shouldRetry = result is ReverseGeocodingResult.Failure.Io
                if (shouldRetry) {
                    delay(retryDelay.toMillis())
                }
            } while (shouldRetry)
        }

    private suspend fun getSingleName(
        locationResult: LocationResult,
        getLocationName: suspend Store<Location, Optional<String>>.(Location) -> Optional<String>
    ): ReverseGeocodingResult =
        when (locationResult) {
            is LocationResult.Success ->
                try {
                    store.getLocationName(locationResult.location)
                        .map { ReverseGeocodingResult.Success(it) }
                        .valueOr { ReverseGeocodingResult.Failure.NotFound }
                } catch (e: IOException) {
                    ReverseGeocodingResult.Failure.Io(e)
                }
            is LocationResult.Failure -> ReverseGeocodingResult.Failure.Location
        }
}
