package se.gustavkarlsson.skylight.android.lib.kpindex

import arrow.core.left
import arrow.core.right
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
            } else store.get(Unit)
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
        store.stream(StoreRequest.cached(Unit, refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loading
                    is StoreResponse.Data -> Loaded(response.value.right())
                    is StoreResponse.Error.Exception ->
                        Loaded(response.error.toKpIndexError().left())

                    is StoreResponse.Error.Message, is StoreResponse.NoNewData ->
                        error("Unsupported response type: $response")
                }
            }
}
