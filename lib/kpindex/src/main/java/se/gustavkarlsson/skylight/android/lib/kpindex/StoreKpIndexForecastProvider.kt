package se.gustavkarlsson.skylight.android.lib.kpindex

import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class StoreKpIndexForecastProvider(private val store: Store<Unit, KpIndexForecast>) : KpIndexForecastProvider {

    override suspend fun get(fresh: Boolean): KpIndexForecastResult {
        val result = getResult(fresh)
        logInfo { "Provided Kp index forecast: $result" }
        return result
    }

    private suspend fun getResult(fresh: Boolean): KpIndexForecastResult {
        return try {
            val forecast = if (fresh) {
                store.fresh(Unit)
            } else {
                store.get(Unit)
            }
            forecast.right()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.toKpIndexError().left()
        }
    }

    override fun stream(): Flow<Loadable<KpIndexForecastResult>> =
        streamResults()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed Kp index forecast: $it" } }

    private fun streamResults(): Flow<Loadable<KpIndexForecastResult>> =
        store.stream(StoreReadRequest.cached(Unit, refresh = false))
            .map { response ->
                when (response) {
                    is StoreReadResponse.Loading -> Loading
                    is StoreReadResponse.Data -> Loaded(response.value.right())
                    is StoreReadResponse.Error.Exception ->
                        Loaded(response.error.toKpIndexError().left())

                    is StoreReadResponse.Error.Message, is StoreReadResponse.NoNewData ->
                        error("Unsupported response type: $response")
                }
            }
}
