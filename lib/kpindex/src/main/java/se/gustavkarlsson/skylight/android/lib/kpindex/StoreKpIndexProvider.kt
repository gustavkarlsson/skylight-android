package se.gustavkarlsson.skylight.android.lib.kpindex

import arrow.core.left
import arrow.core.right
import com.dropbox.android.external.store4.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import java.io.IOException

internal class StoreKpIndexProvider(private val store: Store<Unit, KpIndex>) : KpIndexProvider {

    override suspend fun get(fresh: Boolean): KpIndexResult {
        val result = getResult(fresh)
        logInfo { "Provided Kp index: $result" }
        return result
    }

    private suspend fun getResult(fresh: Boolean): KpIndexResult {
        return try {
            val kpIndex = if (fresh) {
                store.fresh(Unit)
            } else store.get(Unit)
            kpIndex.right()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.toKpIndexError().left()
        }
    }

    override fun stream(): Flow<Loadable<KpIndexResult>> =
        streamResults()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed Kp index: $it" } }

    private fun streamResults(): Flow<Loadable<KpIndexResult>> =
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

private fun Throwable.toKpIndexError(): KpIndexError =
    when (this) {
        is IOException -> KpIndexError.Connectivity
        is ServerResponseException -> KpIndexError.ServerResponse
        else -> KpIndexError.Unknown
    }
