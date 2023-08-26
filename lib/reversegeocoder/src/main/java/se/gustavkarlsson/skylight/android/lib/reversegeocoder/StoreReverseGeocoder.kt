package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.impl.extensions.get
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.approximate
import java.io.IOException
import kotlin.time.Duration

internal class StoreReverseGeocoder(
    private val store: Store<ApproximatedLocation, Option<String>>,
    private val retryDelay: Duration,
    private val approximationMeters: Double,
) : ReverseGeocoder {

    override suspend fun get(location: Location): ReverseGeocodingResult {
        val result = getResult(location)
        logInfo { "Provided location name: $result" }
        return result
    }

    override fun stream(location: Location): Flow<Loadable<ReverseGeocodingResult>> {
        return getNameWithRetry(location)
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed location name: $it" } }
    }

    private fun getNameWithRetry(location: Location): Flow<Loadable<ReverseGeocodingResult>> = flow {
        emit(Loading)
        do {
            val result = getResult(location)
            emit(Loaded(result))
            val shouldRetry = result.fold(
                ifLeft = { error -> error == ReverseGeocodingError.Io },
                ifRight = { false },
            )
            if (shouldRetry) {
                delay(retryDelay)
            }
        } while (shouldRetry)
    }

    private suspend fun getResult(location: Location): ReverseGeocodingResult = Either
        .catch {
            store.get(location.approximate(approximationMeters)).getOrNull()
        }
        .mapLeft { throwable ->
            when (throwable) {
                is IOException -> ReverseGeocodingError.Io
                else -> ReverseGeocodingError.Unknown
            }
        }
        .flatMap { name ->
            name?.right() ?: ReverseGeocodingError.NotFound.left()
        }
}
